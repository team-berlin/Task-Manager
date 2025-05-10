package com.berlin.data.mongodb.datasource

import com.berlin.data.BaseDataSource
import com.berlin.data.mongodb.config.MongoConfig
import com.berlin.domain.model.AuditLog
import com.mongodb.client.model.Filters
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList

class MongoDBAuditLogDataSource(
    private val mongoConfig: MongoConfig
) : BaseDataSource<AuditLog> {

    private val client = mongoConfig.createMongoClient()
    private val database = mongoConfig.getDatabase(client)
    private val collection = mongoConfig.getCollection<AuditLog>(database, "audit_logs")

    override suspend fun getAll(): List<AuditLog> {
        return try {
            collection.find().toList()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getById(id: String): AuditLog? {
        return try {
            collection.find(Filters.eq("_id", id)).firstOrNull()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun update(id: String, entity: AuditLog): Boolean {
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

    override suspend fun write(entity: AuditLog): Boolean {
        return try {
            collection.insertOne(entity).wasAcknowledged()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun writeAll(entities: List<AuditLog>): Boolean {
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