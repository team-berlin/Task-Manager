package com.berlin.data.mongodb.datasource

import com.berlin.data.BaseDataSource
import com.berlin.data.dto.AuditLogDto
import com.berlin.data.mongodb.config.MongoConfig
import com.mongodb.client.model.Filters
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking

class MongoDBAuditLogDataSource(
    private val mongoConfig: MongoConfig
) : BaseDataSource<AuditLogDto> {

    private val client = mongoConfig.createMongoClient()
    private val database = mongoConfig.getDatabase(client)
    private val collection = mongoConfig.getCollection<AuditLogDto>(database, "audit_logs")

    override fun getAll(): List<AuditLogDto> {
        return runBlocking {
            collection.find().toList()
        }
    }

    override fun getById(id: String): AuditLogDto? {
        return runBlocking {
            collection.find(Filters.eq("_id", id)).firstOrNull()
        }
    }

    override fun update(id: String, entity: AuditLogDto): Boolean {
        return runBlocking {
            collection.replaceOne(Filters.eq("_id", id), entity).wasAcknowledged()
        }
    }

    override fun delete(id: String): Boolean {
        return runBlocking {
            collection.deleteOne(Filters.eq("_id", id)).wasAcknowledged()
        }
    }

    override fun write(entity: AuditLogDto): Boolean {
        return runBlocking {
            collection.insertOne(entity).wasAcknowledged()
        }
    }

    override fun writeAll(entities: List<AuditLogDto>): Boolean {
        if (entities.isEmpty()) {
            return false
        }
        return runBlocking {
            collection.insertMany(entities).wasAcknowledged()
        }
    }
}