package com.berlin.data.mongodb.datasource

import com.berlin.data.BaseDataSource
import com.berlin.data.dto.UserDto
import com.berlin.data.mongodb.config.MongoConfig
import com.mongodb.client.model.Filters
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking

class MongoDBUserDataSource(
    private val mongoConfig: MongoConfig
) : BaseDataSource<UserDto> {

    private val client = mongoConfig.createMongoClient()
    private val database = mongoConfig.getDatabase(client)
    private val collection = mongoConfig.getCollection<UserDto>(database, "users")

    override fun getAll(): List<UserDto> {
        return runBlocking {
            collection.find().toList()
        }
    }

    override fun getById(id: String): UserDto? {
        return runBlocking {
            collection.find(Filters.eq("_id", id)).firstOrNull()
        }
    }

    override fun update(id: String, entity: UserDto): Boolean {
        return runBlocking {
            collection.replaceOne(Filters.eq("_id", id), entity).wasAcknowledged()
        }
    }

    override fun delete(id: String): Boolean {
        return runBlocking {
            collection.deleteOne(Filters.eq("_id", id)).wasAcknowledged()
        }
    }

    override fun write(entity: UserDto): Boolean {
        return runBlocking {
            collection.insertOne(entity).wasAcknowledged()
        }
    }

    override fun writeAll(entities: List<UserDto>): Boolean {
        if (entities.isEmpty()) {
            return false
        }
        return runBlocking {
            collection.insertMany(entities).wasAcknowledged()
        }
    }
}