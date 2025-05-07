package com.berlin.data.mongodb.datasource

import com.berlin.data.BaseDataSource
import com.berlin.data.mongodb.config.MongoConfig
import com.berlin.domain.model.State
import com.mongodb.client.model.Filters
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking

class MongoDBStateDataSource(
    private val mongoConfig: MongoConfig
) : BaseDataSource<State> {

    private val client = mongoConfig.createMongoClient()
    private val database = mongoConfig.getDatabase(client)
    private val collection = mongoConfig.getCollection<State>(database, "states")

    override fun getAll(): List<State> {
        return runBlocking {
            collection.find().toList()
        }
    }

    override fun getById(id: String): State? {
        return runBlocking {
            collection.find(Filters.eq("_id", id)).firstOrNull()
        }
    }

    override fun update(id: String, entity: State): Boolean {
        return runBlocking {
            collection.replaceOne(Filters.eq("_id", id), entity).wasAcknowledged()
        }
    }

    override fun delete(id: String): Boolean {
        return runBlocking {
            collection.deleteOne(Filters.eq("_id", id)).wasAcknowledged()
        }
    }

    override fun write(entity: State): Boolean {
        return runBlocking {
            collection.insertOne(entity).wasAcknowledged()
        }
    }

    override fun writeAll(entities: List<State>): Boolean {
        if (entities.isEmpty()) {
            return false
        }
        return runBlocking {
            collection.insertMany(entities).wasAcknowledged()
        }
    }
}