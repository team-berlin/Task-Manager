package com.berlin.data.mongodb.datasource

import com.berlin.data.dto.AuditLogDto
import com.berlin.data.mongodb.config.MongoConfig
import com.berlin.domain.model.AuditLog
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.InsertManyResult
import com.mongodb.client.result.InsertOneResult
import com.mongodb.client.result.UpdateResult
import kotlinx.coroutines.flow.FlowCollector
import org.bson.conversions.Bson
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle

@TestInstance(Lifecycle.PER_CLASS)
class MongoDBAuditLogDataSourceTest {

    private lateinit var dataSource: MongoDBAuditLogDataSource
    private val mockMongoConfig = mockk<MongoConfig>()
    private val mockCollection = mockk<com.mongodb.kotlin.client.coroutine.MongoCollection<AuditLogDto>>()
    private val mockMongoClient = mockk<com.mongodb.kotlin.client.coroutine.MongoClient>()
    private val mockMongoDatabase = mockk<com.mongodb.kotlin.client.coroutine.MongoDatabase>()
    private val mockFindPublisher = mockk<com.mongodb.kotlin.client.coroutine.FindFlow<AuditLogDto>>()

    private val mockAuditLog = AuditLogDto(
        id = "log1",
        timestamp = 1717027200,
        createdByUserId = "user1",
        auditAction = AuditLog.AuditAction.CREATE,
        changesDescription = "Initial creation",
        entityType = AuditLog.EntityType.TASK,
        entityId = "task1"
    )

    private val mockAuditLogs = listOf(
        mockAuditLog,
        AuditLogDto(
            id = "log2",
            timestamp = 1717027300,
            createdByUserId = "user2",
            auditAction = AuditLog.AuditAction.UPDATE,
            changesDescription = "Status changed",
            entityType = AuditLog.EntityType.TASK,
            entityId = "task1"
        )
    )

    @BeforeEach
    fun setUp() {
        every { mockMongoConfig.createMongoClient() } returns mockMongoClient
        every { mockMongoConfig.getDatabase(mockMongoClient) } returns mockMongoDatabase
        every { mockMongoConfig.getCollection<AuditLogDto>(mockMongoDatabase, "audit_logs") } returns mockCollection

        coEvery { mockFindPublisher.collect(any()) } coAnswers {
            val collector = arg<FlowCollector<AuditLogDto>>(0)
            collector.emit(mockAuditLog)
        }

        dataSource = MongoDBAuditLogDataSource(mockMongoConfig)
    }

    @Test
    fun `getAll should return list of audit logs`() {
        // Given
        coEvery { mockCollection.find() } returns mockFindPublisher
        coEvery { mockFindPublisher.collect(any()) } coAnswers {
            val collector = arg<FlowCollector<AuditLogDto>>(0)
            mockAuditLogs.forEach { collector.emit(it) }
        }

        // When
        val result = dataSource.getAll()

        // Then
        assertThat(result).isEqualTo(mockAuditLogs)
    }

    @Test
    fun `getById should return audit log when found`() {
        // Given
        coEvery { mockCollection.find(any<Bson>()) } returns mockFindPublisher
        coEvery { mockFindPublisher.collect(any()) } coAnswers {
            val collector = arg<FlowCollector<AuditLogDto>>(0)
            collector.emit(mockAuditLog)
        }

        // When
        val result = dataSource.getById("log1")

        // Then
        assertThat(result).isEqualTo(mockAuditLog)
    }

    @Test
    fun `getById should return null when not found`() {
        // Given
        coEvery { mockCollection.find(any<Bson>()) } returns mockFindPublisher
        coEvery { mockFindPublisher.collect(any()) } coAnswers { }

        // When
        val result = dataSource.getById("nonexistent")

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun `update should return true when acknowledged`() {
        // Given
        val mockUpdateResult = mockk<UpdateResult>()
        every { mockUpdateResult.wasAcknowledged() } returns true

        coEvery {
            mockCollection.replaceOne(
                any<Bson>(),
                any<AuditLogDto>(),
                any()
            )
        } returns mockUpdateResult

        // When
        val result = dataSource.update("log1", mockAuditLog)

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `update should return false when not acknowledged`() {
        // Given
        val mockUpdateResult = mockk<UpdateResult>()
        every { mockUpdateResult.wasAcknowledged() } returns false
        coEvery { mockCollection.replaceOne(any<Bson>(), any<AuditLogDto>(), any()) } returns mockUpdateResult

        // When
        val result = dataSource.update("log1", mockAuditLog)

        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `delete should return true when acknowledged`() {
        // Given
        val mockDeleteResult = mockk<DeleteResult>()
        every { mockDeleteResult.wasAcknowledged() } returns true
        coEvery { mockCollection.deleteOne(any<Bson>(), any()) } returns mockDeleteResult

        // When
        val result = dataSource.delete("log1")

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `delete should return false when not acknowledged`() {
        // Given
        val mockDeleteResult = mockk<DeleteResult>()
        every { mockDeleteResult.wasAcknowledged() } returns false
        coEvery { mockCollection.deleteOne(any<Bson>(), any()) } returns mockDeleteResult

        // When
        val result = dataSource.delete("log1")

        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `write should return true when acknowledged`() {
        // Given
        val mockInsertOneResult = mockk<InsertOneResult>()
        every { mockInsertOneResult.wasAcknowledged() } returns true
        coEvery { mockCollection.insertOne(any<AuditLogDto>(), any()) } returns mockInsertOneResult

        // When
        val result = dataSource.write(mockAuditLog)

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `write should return false when not acknowledged`() {
        // Given
        val mockInsertOneResult = mockk<InsertOneResult>()
        every { mockInsertOneResult.wasAcknowledged() } returns false
        coEvery { mockCollection.insertOne(any<AuditLogDto>(), any()) } returns mockInsertOneResult

        // When
        val result = dataSource.write(mockAuditLog)

        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `writeAll should return true when acknowledged`() {
        // Given
        val mockInsertManyResult = mockk<InsertManyResult>()
        every { mockInsertManyResult.wasAcknowledged() } returns true
        coEvery { mockCollection.insertMany(any<List<AuditLogDto>>(), any()) } returns mockInsertManyResult

        // When
        val result = dataSource.writeAll(mockAuditLogs)

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `writeAll should return false when not acknowledged`() {
        // Given
        val mockInsertManyResult = mockk<InsertManyResult>()
        every { mockInsertManyResult.wasAcknowledged() } returns false
        coEvery { mockCollection.insertMany(any<List<AuditLogDto>>(), any()) } returns mockInsertManyResult

        // When
        val result = dataSource.writeAll(mockAuditLogs)

        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `writeAll should return false when list is empty`() {
        // When
        val result = dataSource.writeAll(emptyList())

        // Then
        assertThat(result).isFalse()
    }
}