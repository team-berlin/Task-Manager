package com.berlin.data.mongodb.datasource

import com.berlin.data.mongodb.config.MongoConfig
import com.berlin.domain.model.Task
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.InsertManyResult
import com.mongodb.client.result.InsertOneResult
import com.mongodb.client.result.UpdateResult
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.bson.conversions.Bson
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle

@TestInstance(Lifecycle.PER_CLASS)
class MongoDBTaskDataSourceTest {

    private lateinit var dataSource: MongoDBTaskDataSource
    private val mockMongoConfig = mockk<MongoConfig>()
    private val mockCollection = mockk<com.mongodb.kotlin.client.coroutine.MongoCollection<Task>>()
    private val mockMongoClient = mockk<com.mongodb.kotlin.client.coroutine.MongoClient>()
    private val mockMongoDatabase = mockk<com.mongodb.kotlin.client.coroutine.MongoDatabase>()
    private val mockFindPublisher = mockk<com.mongodb.kotlin.client.coroutine.FindFlow<Task>>()

    private val mockTask = Task(
        id = "task1",
        projectId = "project1",
        title = "Test Task",
        description = "Task description",
        stateId = "todo",
        assignedToUserId = "user1",
        createByUserId = "user2"
    )

    private val mockTasks = listOf(
        mockTask,
        Task(
            id = "task2",
            projectId = "project1",
            title = "Another Task",
            description = null,
            stateId = "in_progress",
            assignedToUserId = "user3",
            createByUserId = "user2"
        )
    )

    @BeforeEach
    fun setUp() {
        every { mockMongoConfig.createMongoClient() } returns mockMongoClient
        every { mockMongoConfig.getDatabase(mockMongoClient) } returns mockMongoDatabase
        every { mockMongoConfig.getCollection<Task>(mockMongoDatabase, "tasks") } returns mockCollection

        coEvery { mockFindPublisher.collect(any()) } coAnswers {
            val collector = arg<FlowCollector<Task>>(0)
            collector.emit(mockTask)
        }

        dataSource = MongoDBTaskDataSource(mockMongoConfig)
    }

    @Test
    fun `getAll should return list of tasks`() {
        // Given
        coEvery { mockCollection.find() } returns mockFindPublisher
        coEvery { mockFindPublisher.collect(any()) } coAnswers {
            val collector = arg<FlowCollector<Task>>(0)
            mockTasks.forEach { collector.emit(it) }
        }

        // When
        val result = dataSource.getAll()

        // Then
        assertEquals(mockTasks, result)
    }

    @Test
    fun `getById should return task when found`() {
        // Given
        coEvery { mockCollection.find(any<Bson>()) } returns mockFindPublisher
        coEvery { mockFindPublisher.collect(any()) } coAnswers {
            val collector = arg<FlowCollector<Task>>(0)
            collector.emit(mockTask)
        }

        // When
        val result = dataSource.getById("task1")

        // Then
        assertEquals(mockTask, result)
    }

    @Test
    fun `getById should return null when not found`() {
        // Given
        coEvery { mockCollection.find(any<Bson>()) } returns mockFindPublisher
        coEvery { mockFindPublisher.collect(any()) } coAnswers { } // Emit nothing

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
                any(),
                any()
            )
        } returns mockUpdateResult

        // When
        val result = dataSource.update("task1", mockTask)

        // Then
        assertTrue(result)
    }

    @Test
    fun `update should return false when not acknowledged`() {
        // Given
        val mockUpdateResult = mockk<UpdateResult>()
        every { mockUpdateResult.wasAcknowledged() } returns false
        coEvery { mockCollection.replaceOne(any<Bson>(), any(), any()) } returns mockUpdateResult

        // When
        val result = dataSource.update("task1", mockTask)

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
        val result = dataSource.delete("task1")

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
        val result = dataSource.delete("task1")

        // Then
        assertFalse(result)
    }

    @Test
    fun `write should return true when acknowledged`() {
        // Given
        val mockInsertOneResult = mockk<InsertOneResult>()
        every { mockInsertOneResult.wasAcknowledged() } returns true
        coEvery { mockCollection.insertOne(any<Task>(), any()) } returns mockInsertOneResult

        // When
        val result = dataSource.write(mockTask)

        // Then
        assertTrue(result)
    }

    @Test
    fun `write should return false when not acknowledged`() {
        // Given
        val mockInsertOneResult = mockk<InsertOneResult>()
        every { mockInsertOneResult.wasAcknowledged() } returns false
        coEvery { mockCollection.insertOne(any<Task>(), any()) } returns mockInsertOneResult

        // When
        val result = dataSource.write(mockTask)

        // Then
        assertFalse(result)
    }

    @Test
    fun `writeAll should return true when acknowledged`() {
        // Given
        val mockInsertManyResult = mockk<InsertManyResult>()
        every { mockInsertManyResult.wasAcknowledged() } returns true
        coEvery { mockCollection.insertMany(any<List<Task>>(), any()) } returns mockInsertManyResult

        // When
        val result = dataSource.writeAll(mockTasks)

        // Then
        assertTrue(result)
    }

    @Test
    fun `writeAll should return false when not acknowledged`() {
        // Given
        val mockInsertManyResult = mockk<InsertManyResult>()
        every { mockInsertManyResult.wasAcknowledged() } returns false
        coEvery { mockCollection.insertMany(any<List<Task>>(), any()) } returns mockInsertManyResult

        // When
        val result = dataSource.writeAll(mockTasks)

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