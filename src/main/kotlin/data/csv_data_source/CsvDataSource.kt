package com.berlin.data.csv_data_source

import com.berlin.data.BaseDataSource
import com.berlin.data.BaseSchema
import com.opencsv.CSVReaderBuilder
import com.opencsv.CSVWriter
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.io.File
import java.io.FileReader
import java.io.FileWriter

open class CsvDataSource<T>(
    private val rootDirectory: String,
    private val schema: BaseSchema<T>,
) : BaseDataSource<T> {

    val csvFile: File
        get() = File(rootDirectory, schema.fileName)

    override suspend fun getAll(): List<T> = when {
        !csvFile.exists() -> emptyList()
        else -> Result.run {
            coroutineScope {
                FileReader(csvFile).use { reader ->
                    CSVReaderBuilder(reader)
                        .withSkipLines(1)
                        .build()
                        .use { csvReader ->
                            csvReader
                                .asSequence()
                                .filter { it.isNotEmpty() }
                                .map { it.toList() }
                                .toList()
                                .map { row -> async { schema.fromRow(row) } }
                                .awaitAll()
                                .filterNotNull()
                        }
                }
            }
        }
    }

    override suspend fun getById(id: String): T? = when {
        !csvFile.exists() -> null
        else -> Result.runCatching {
            getAll().find { entity -> schema.getId(entity) == id }
        }.getOrNull()
    }

    override suspend fun update(id: String, entity: T): Boolean = Result.runCatching {
        schema.toRow(entity).takeIf { it.isNotEmpty() } ?: return false

        if (!csvFile.exists()) return false

        val entities = getAll()
        val entityIndex = entities.indexOfFirst { schema.getId(it) == id }

        if (entityIndex == -1) return false

        val updatedEntities = entities.mapIndexed { index, currentEntity ->
            if (index == entityIndex) entity else currentEntity
        }

        writeEntitiesToFile(updatedEntities)
    }.getOrDefault(false)

    override suspend fun delete(id: String): Boolean = Result.runCatching {
        if (!csvFile.exists()) return false

        val entities = getAll()
        val entityExists = entities.any { schema.getId(it) == id }

        if (!entityExists) return false

        val remainingEntities = entities.filter { schema.getId(it) != id }

        writeEntitiesToFile(remainingEntities)
    }.getOrDefault(false)

    override suspend fun write(entity: T): Boolean = Result.runCatching {
        val rowToWrite = schema.toRow(entity).takeIf { it.isNotEmpty() } ?: return false

        csvFile.parentFile?.mkdirs()

        when {
            !csvFile.exists() -> FileWriter(csvFile).use { writer ->
                CSVWriter(writer).use { csvWriter ->
                    csvWriter.writeNext(schema.header.toTypedArray())
                    csvWriter.writeNext(rowToWrite.toTypedArray())
                }
            }

            else -> FileWriter(csvFile, true).use { writer ->
                CSVWriter(writer).use { csvWriter ->
                    csvWriter.writeNext(rowToWrite.toTypedArray())
                }
            }
        }
        true
    }.getOrDefault(false)

    override suspend fun writeAll(entities: List<T>): Boolean = when {
        entities.isEmpty() -> false
        else -> Result.runCatching {
            csvFile.parentFile?.mkdirs()
            writeEntitiesToFile(entities)
        }.getOrDefault(false)
    }

    private suspend fun writeEntitiesToFile(entities: List<T>): Boolean = Result.runCatching {
        FileWriter(csvFile).use { writer ->
            CSVWriter(writer).use { csvWriter ->
                csvWriter.writeNext(schema.header.toTypedArray())
                entities
                    .mapNotNull { entity ->
                        schema.toRow(entity).takeIf { it.isNotEmpty() }
                    }
                    .forEach { csvWriter.writeNext(it.toTypedArray()) }
            }
        }
        true
    }.getOrDefault(false)
}