package com.berlin.data.mongodb.datasource

import com.berlin.data.BaseDataSource
import com.berlin.data.mongodb.config.MongoConfig
import com.berlin.domain.model.AuditLog
import com.mongodb.client.model.Filters
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking

class MongoDBauditLogDataSource(
    private val mongoConfig: MongoConfig
) : BaseDataSource<AuditLog> {

    private val client = mongoConfig.createMongoClient()
    private val database = mongoConfig.getDatabase(client)
    private val collection = mongoConfig.getCollection<AuditLog>(database, "audit_logs")

    override fun getAll(): List<AuditLog> {
        return runBlocking {
            collection.find().toList()
        }
    }

    override fun getById(id: String): AuditLog? {
        return runBlocking {
            collection.find(Filters.eq("_id", id)).firstOrNull()
        }
    }

    override fun update(id: String, entity: AuditLog): Boolean {
        return runBlocking {
            collection.replaceOne(Filters.eq("_id", id), entity).wasAcknowledged()
        }
    }

    override fun delete(id: String): Boolean {
        return runBlocking {
            collection.deleteOne(Filters.eq("_id", id)).wasAcknowledged()
        }
    }

    override fun write(entity: AuditLog): Boolean {
        return runBlocking {
            collection.insertOne(entity).wasAcknowledged()
        }
    }

    override fun writeAll(entities: List<AuditLog>): Boolean {
        if (entities.isEmpty()) {
            return false
        }
        return runBlocking {
            collection.insertMany(entities).wasAcknowledged()
        }
    }
}