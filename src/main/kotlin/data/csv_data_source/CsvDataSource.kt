package com.berlin.data.csv_data_source

import com.berlin.data.BaseDataSource
import com.berlin.data.BaseSchema

class CsvDataSource<T>(
    private val rootDirectory: String,
    private val schema: BaseSchema<T>
) : BaseDataSource<T> {

    override fun getAll(): List<T> {
        return emptyList()
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