package com.berlin.data.mongodb.datasource

import com.berlin.data.mongodb.config.MongoConfig
import com.berlin.domain.model.TaskState
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
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
class MongoDBStateDataSourceTest {

    private lateinit var dataSource: MongoDBStateDataSource
    private val mockMongoConfig = mockk<MongoConfig>()
    private val mockCollection = mockk<com.mongodb.kotlin.client.coroutine.MongoCollection<TaskState>>()
    private val mockMongoClient = mockk<com.mongodb.kotlin.client.coroutine.MongoClient>()
    private val mockMongoDatabase = mockk<com.mongodb.kotlin.client.coroutine.MongoDatabase>()
    private val mockFindPublisher = mockk<com.mongodb.kotlin.client.coroutine.FindFlow<TaskState>>()

    private val mockState = TaskState(
        id = "state1",
        name = "Todo",
        projectId = "project1"
    )

    private val mockStates = listOf(
        mockState,
        TaskState(
            id = "state2",
            name = "In Progress",
            projectId = "project1"
        )
    )

    @BeforeEach
    fun setUp() {
        every { mockMongoConfig.createMongoClient() } returns mockMongoClient
        every { mockMongoConfig.getDatabase(mockMongoClient) } returns mockMongoDatabase
        every { mockMongoConfig.getCollection<TaskState>(mockMongoDatabase, "states") } returns mockCollection

        coEvery { mockFindPublisher.collect(any()) } coAnswers {
            val collector = arg<FlowCollector<TaskState>>(0)
            collector.emit(mockState)
        }

        dataSource = MongoDBStateDataSource(mockMongoConfig)
    }

    @Test
    fun `getAll should return list of states`() {
        // Given
        coEvery { mockCollection.find() } returns mockFindPublisher
        coEvery { mockFindPublisher.collect(any()) } coAnswers {
            val collector = arg<FlowCollector<TaskState>>(0)
            mockStates.forEach { collector.emit(it) }
        }

        // When
        val result = dataSource.getAll()

        // Then
        assertEquals(mockStates, result)
    }

    @Test
    fun `getById should return state when found`() {
        // Given
        coEvery { mockCollection.find(any<Bson>()) } returns mockFindPublisher
        coEvery { mockFindPublisher.collect(any()) } coAnswers {
            val collector = arg<FlowCollector<TaskState>>(0)
            collector.emit(mockState)
        }

        // When
        val result = dataSource.getById("state1")

        // Then
        assertEquals(mockState, result)
    }

    @Test
    fun `getById should return null when not found`() {
        // Given
        coEvery { mockCollection.find(any<Bson>()) } returns mockFindPublisher
        coEvery { mockFindPublisher.collect(any()) } coAnswers { }

        // When
        val result = dataSource.getById("nonexistent")

        // Then
        assertNull(result)
    }

    @Test
    fun `update should return true when acknowledged`() {
        // Given
        val mockUpdateResult = mockk<UpdateResult>()
        every { mockUpdateResult.wasAcknowledged() } returns true

        coEvery {
            mockCollection.replaceOne(
                any<Bson>(),
                any<TaskState>(),
                any()
            )
        } returns mockUpdateResult

        // When
        val result = dataSource.update("state1", mockState)

        // Then
        assertTrue(result)
    }

    @Test
    fun `update should return false when not acknowledged`() {
        // Given
        val mockUpdateResult = mockk<UpdateResult>()
        every { mockUpdateResult.wasAcknowledged() } returns false
        coEvery { mockCollection.replaceOne(any<Bson>(), any<TaskState>(), any()) } returns mockUpdateResult

        // When
        val result = dataSource.update("state1", mockState)

        // Then
        assertFalse(result)
    }

    @Test
    fun `delete should return true when acknowledged`() {
        // Given
        val mockDeleteResult = mockk<DeleteResult>()
        every { mockDeleteResult.wasAcknowledged() } returns true
        coEvery { mockCollection.deleteOne(any<Bson>(), any()) } returns mockDeleteResult

        // When
        val result = dataSource.delete("state1")

        // Then
        assertTrue(result)
    }

    @Test
    fun `delete should return false when not acknowledged`() {
        // Given
        val mockDeleteResult = mockk<DeleteResult>()
        every { mockDeleteResult.wasAcknowledged() } returns false
        coEvery { mockCollection.deleteOne(any<Bson>(), any()) } returns mockDeleteResult

        // When
        val result = dataSource.delete("state1")

        // Then
        assertFalse(result)
    }

    @Test
    fun `write should return true when acknowledged`() {
        // Given
        val mockInsertOneResult = mockk<InsertOneResult>()
        every { mockInsertOneResult.wasAcknowledged() } returns true
        coEvery { mockCollection.insertOne(any<TaskState>(), any()) } returns mockInsertOneResult

        // When
        val result = dataSource.write(mockState)

        // Then
        assertTrue(result)
    }

    @Test
    fun `write should return false when not acknowledged`() {
        // Given
        val mockInsertOneResult = mockk<InsertOneResult>()
        every { mockInsertOneResult.wasAcknowledged() } returns false
        coEvery { mockCollection.insertOne(any<TaskState>(), any()) } returns mockInsertOneResult

        // When
        val result = dataSource.write(mockState)

        // Then
        assertFalse(result)
    }

    @Test
    fun `writeAll should return true when acknowledged`() {
        // Given
        val mockInsertManyResult = mockk<InsertManyResult>()
        every { mockInsertManyResult.wasAcknowledged() } returns true
        coEvery { mockCollection.insertMany(any<List<TaskState>>(), any()) } returns mockInsertManyResult

        // When
        val result = dataSource.writeAll(mockStates)

        // Then
        assertTrue(result)
    }

    @Test
    fun `writeAll should return false when not acknowledged`() {
        // Given
        val mockInsertManyResult = mockk<InsertManyResult>()
        every { mockInsertManyResult.wasAcknowledged() } returns false
        coEvery { mockCollection.insertMany(any<List<TaskState>>(), any()) } returns mockInsertManyResult

        // When
        val result = dataSource.writeAll(mockStates)

        // Then
        assertFalse(result)
    }

    @Test
    fun `writeAll should return false when list is empty`() {
        // When
        val result = dataSource.writeAll(emptyList())

        // Then
        assertFalse(result)
    }
}