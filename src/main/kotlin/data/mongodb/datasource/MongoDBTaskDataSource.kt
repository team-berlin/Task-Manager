package com.berlin.data.mongodb.datasource

import com.berlin.data.BaseDataSource
import com.berlin.data.mongodb.config.MongoConfig
import com.berlin.domain.model.Task
import com.mongodb.client.model.Filters
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList

class MongoDBTaskDataSource(
    private val mongoConfig: MongoConfig
) : BaseDataSource<Task> {

    private val client = mongoConfig.createMongoClient()
    private val database = mongoConfig.getDatabase(client)
    private val collection = mongoConfig.getCollection<Task>(database, "tasks")

    override suspend fun getAll(): List<Task> {
        return try {
            collection.find().toList()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getById(id: String): Task? {
        return try {
            collection.find(Filters.eq("_id", id)).firstOrNull()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun update(id: String, entity: Task): Boolean {
        return try {
            collection.replaceOne(Filters.eq("_id", id), entity).wasAcknowledged()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun delete(id: String): Boolean {
        return try {
            collection.deleteOne(Filters.eq("_id", id)).wasAcknowledged()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun write(entity: Task): Boolean {
        return try {
            collection.insertOne(entity).wasAcknowledged()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun writeAll(entities: List<Task>): Boolean {
        if (entities.isEmpty()) {
            return false
        }
        return try {
            collection.insertMany(entities).wasAcknowledged()
        } catch (e: Exception) {
            throw e
        }
    }
}