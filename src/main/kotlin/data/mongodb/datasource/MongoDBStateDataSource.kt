package com.berlin.data.mongodb.datasource

import com.berlin.data.BaseDataSource
import com.berlin.data.dto.TaskStateDto
import com.berlin.data.mongodb.config.MongoConfig
import com.berlin.domain.model.TaskState
import com.mongodb.client.model.Filters
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking

class MongoDBStateDataSource(
    private val mongoConfig: MongoConfig
) : BaseDataSource<TaskStateDto> {

    private val client = mongoConfig.createMongoClient()
    private val database = mongoConfig.getDatabase(client)
    private val collection = mongoConfig.getCollection<TaskStateDto>(database, "states")

    override fun getAll(): List<TaskStateDto> {
        return runBlocking {
            collection.find().toList()
        }
    }

    override fun getById(id: String): TaskStateDto? {
        return runBlocking {
            collection.find(Filters.eq("_id", id)).firstOrNull()
        }
    }

    override fun update(id: String, entity: TaskStateDto): Boolean {
        return runBlocking {
            collection.replaceOne(Filters.eq("_id", id), entity).wasAcknowledged()
        }
    }

    override fun delete(id: String): Boolean {
        return runBlocking {
            collection.deleteOne(Filters.eq("_id", id)).wasAcknowledged()
        }
    }

    override fun write(entity: TaskStateDto): Boolean {
        return runBlocking {
            collection.insertOne(entity).wasAcknowledged()
        }
    }

    override fun writeAll(entities: List<TaskStateDto>): Boolean {
        if (entities.isEmpty()) {
            return false
        }
        return runBlocking {
            collection.insertMany(entities).wasAcknowledged()
        }
    }
}