package com.berlin.data.mongodb_data_source

import com.berlin.data.BaseDataSource
import com.berlin.data.BaseSchema

open class MongoDBDataSource<T>(
    private val connectionString: String,
    private val databaseName: String,
    private val schema: BaseSchema<T>,
) : BaseDataSource<T> {
    override fun getAll(): List<T> {
        TODO("Not yet implemented")
    }

    override fun getById(id: String): T? {
        TODO("Not yet implemented")
    }

    override fun update(id: String, entity: T): Boolean {
        TODO("Not yet implemented")
    }

    override fun delete(id: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun write(entity: T): Boolean {
        TODO("Not yet implemented")
    }

    override fun writeAll(entities: List<T>): Boolean {
        TODO("Not yet implemented")
    }
}