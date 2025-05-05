package com.berlin.data.mongodb_data_source

import com.berlin.data.BaseDataSource
import com.berlin.data.BaseSchema
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import com.mongodb.client.model.ReplaceOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bson.Document
import org.bson.types.ObjectId
import java.util.logging.Level
import java.util.logging.Logger

open class MongoDBDataSource<T>(
    private val connectionString: String,
    private val databaseName: String,
    private val schema: BaseSchema<T>,
) : BaseDataSource<T> {
    override suspend fun getAll(): List<T> {
        TODO("Not yet implemented")
    }

    override suspend fun getById(id: String): T? {
        TODO("Not yet implemented")
    }

    override suspend fun update(id: String, entity: T): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun write(entity: T): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun writeAll(entities: List<T>): Boolean {
        TODO("Not yet implemented")
    }


}