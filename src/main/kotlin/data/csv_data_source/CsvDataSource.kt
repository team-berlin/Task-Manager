package com.berlin.data.csv_data_source

import com.berlin.data.BaseDataSource
import com.berlin.data.BaseSchema
import com.opencsv.CSVReaderBuilder
import com.opencsv.CSVWriter
import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.FileWriter

class CsvDataSource<T>(
    private val rootDirectory: String, private val schema: BaseSchema<T>
) : BaseDataSource<T> {

    private val csvFile: File
        get() = File(rootDirectory, schema.fileName)

    override fun getAll(): List<T> {
        if (!csvFile.exists())
            throw FileNotFoundException("File not found: ${csvFile.name}")

        return FileReader(csvFile).use { reader ->
            CSVReaderBuilder(reader)
                .withSkipLines(1)
                .build()
                .use { csvReader ->
                    csvReader
                        .asSequence()
                        .filter { row -> row.isNotEmpty() }
                        .mapNotNull { row -> schema.fromRow(row.toList()) }
                        .toList()
                }
        }

    }

    override fun getById(id: String): T? {
        if (!csvFile.exists()) {
            return null
        }

        return getAll().find { entity ->
            schema.getId(entity) == id
        }
    }

    override fun update(id: String, entity: T): Boolean {
        if (!csvFile.exists()) return false

        val rowToWrite = schema.toRow(entity)
        if (rowToWrite.isEmpty()) return false

        return try {
            val entities = getAll()
            val entityIndex = entities.indexOfFirst { schema.getId(it) == id }

            if (entityIndex == -1) return false

            val updatedEntities = entities.mapIndexed { index, currentEntity ->
                if (index == entityIndex) entity else currentEntity
            }

            FileWriter(csvFile).use { writer ->
                CSVWriter(writer).use { csvWriter ->
                    csvWriter.writeNext(schema.header.toTypedArray())
                    updatedEntities
                        .map { schema.toRow(it) }
                        .filter { it.isNotEmpty() }
                        .forEach { csvWriter.writeNext(it.toTypedArray()) }
                }
            }

            true
        } catch (e: Exception) {
            false
        }
    }

    override fun delete(id: String): Boolean {
        return false
    }

    override fun write(entity: T): Boolean {
        return false
    }

    override fun writeAll(entities: List<T>): Boolean {
        return false
    }


}