package com.berlin.data

import com.opencsv.CSVReaderBuilder
import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader


abstract class CsvDataSource<T>(
    private val filePath: String,
    private val headers: List<String>,
    private val toRow: (T) -> List<String>,
    private val fromRow: (List<String>) -> T?
) : BaseRepository<T> {
    override fun write(row: T): Boolean {
        TODO("Not yet implemented")
    }

    override fun writeAll(rows: List<T>): Boolean {
        TODO("Not yet implemented")
    }

    override fun readAll(): List<T> {
        val file = File(filePath)
        if (!file.exists())
            throw FileNotFoundException("File not found: $filePath")

        return FileReader(file).use { reader ->
            CSVReaderBuilder(reader)
                .withSkipLines(1)
                .build()
                .use { csvReader ->
                    csvReader
                        .asSequence()
                        .filter { row->row.isNotEmpty() }
                        .mapNotNull { row -> fromRow(row.toList()) }
                        .toList()
                }
        }
    }

    override fun deleteById(id: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun update(id: String, row: T): Boolean {
        TODO("Not yet implemented")
    }

}