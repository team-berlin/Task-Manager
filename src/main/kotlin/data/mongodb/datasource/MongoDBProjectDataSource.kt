package com.berlin.data.mongodb.datasource

import com.berlin.data.BaseDataSource
import com.berlin.data.mongodb.config.MongoConfig
import com.berlin.domain.model.Project
import com.mongodb.client.model.Filters
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList

class MongoDBProjectDataSource(
    private val mongoConfig: MongoConfig
) : BaseDataSource<Project> {

    private val client = mongoConfig.createMongoClient()
    private val database = mongoConfig.getDatabase(client)
    private val collection = mongoConfig.getCollection<Project>(database, "projects")

    override suspend fun getAll(): List<Project> {
        return try {
            collection.find().toList()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getById(id: String): Project? {
        return try {
            collection.find(Filters.eq("_id", id)).firstOrNull()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun update(id: String, entity: Project): Boolean {
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

    override suspend fun write(entity: Project): Boolean {
        return try {
            collection.insertOne(entity).wasAcknowledged()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun writeAll(entities: List<Project>): Boolean {
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