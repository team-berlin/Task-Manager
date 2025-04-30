package com.berlin.data.csv_data_source

import com.berlin.data.BaseDataSource
import com.berlin.data.BaseSchema
import com.opencsv.CSVReaderBuilder
import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader

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
        return null
    }

    override fun update(id: String, entity: T): Boolean {
        return false
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