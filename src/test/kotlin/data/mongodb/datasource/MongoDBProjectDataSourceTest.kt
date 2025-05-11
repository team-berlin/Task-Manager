package com.berlin.data.mongodb.datasource

import com.berlin.data.mongodb.config.MongoConfig
import com.berlin.domain.model.Project
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
class MongoDBProjectDataSourceTest {

    private lateinit var dataSource: MongoDBProjectDataSource
    private val mockMongoConfig = mockk<MongoConfig>()
    private val mockCollection = mockk<com.mongodb.kotlin.client.coroutine.MongoCollection<Project>>()
    private val mockMongoClient = mockk<com.mongodb.kotlin.client.coroutine.MongoClient>()
    private val mockMongoDatabase = mockk<com.mongodb.kotlin.client.coroutine.MongoDatabase>()
    private val mockFindPublisher = mockk<com.mongodb.kotlin.client.coroutine.FindFlow<Project>>()

    private val mockProject = Project(
        id = "project1",
        name = "Task Manager",
        description = "Manage tasks efficiently",
        statesId = listOf("state1", "state2"),
        tasksId = listOf("task1", "task2")
    )

    private val mockProjects = listOf(
        mockProject,
        Project(
            id = "project2",
            name = "Marketing Campaign",
            description = null,
            statesId = emptyList(),
            tasksId = null
        )
    )

    @BeforeEach
    fun setUp() {
        every { mockMongoConfig.createMongoClient() } returns mockMongoClient
        every { mockMongoConfig.getDatabase(mockMongoClient) } returns mockMongoDatabase
        every { mockMongoConfig.getCollection<Project>(mockMongoDatabase, "projects") } returns mockCollection

        coEvery { mockFindPublisher.collect(any()) } coAnswers {
            val collector = arg<FlowCollector<Project>>(0)
            collector.emit(mockProject)
        }

        dataSource = MongoDBProjectDataSource(mockMongoConfig)
    }

    @Test
    fun `getAll should return list of projects`() {
        // Given
        coEvery { mockCollection.find() } returns mockFindPublisher
        coEvery { mockFindPublisher.collect(any()) } coAnswers {
            val collector = arg<FlowCollector<Project>>(0)
            mockProjects.forEach { collector.emit(it) }
        }

        // When
        val result = dataSource.getAll()

        // Then
        assertThat(result).isEqualTo(mockProjects)
    }

    @Test
    fun `getById should return project when found`() {
        // Given
        coEvery { mockCollection.find(any<Bson>()) } returns mockFindPublisher
        coEvery { mockFindPublisher.collect(any()) } coAnswers {
            val collector = arg<FlowCollector<Project>>(0)
            collector.emit(mockProject)
        }

        // When
        val result = dataSource.getById("project1")

        // Then
        assertThat(result).isEqualTo(mockProject)
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
                any<Project>(),
                any()
            )
        } returns mockUpdateResult

        // When
        val result = dataSource.update("project1", mockProject)

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `update should return false when not acknowledged`() {
        // Given
        val mockUpdateResult = mockk<UpdateResult>()
        every { mockUpdateResult.wasAcknowledged() } returns false
        coEvery { mockCollection.replaceOne(any<Bson>(), any<Project>(), any()) } returns mockUpdateResult

        // When
        val result = dataSource.update("project1", mockProject)

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
        val result = dataSource.delete("project1")

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
        val result = dataSource.delete("project1")

        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `write should return true when acknowledged`() {
        // Given
        val mockInsertOneResult = mockk<InsertOneResult>()
        every { mockInsertOneResult.wasAcknowledged() } returns true
        coEvery { mockCollection.insertOne(any<Project>(), any()) } returns mockInsertOneResult

        // When
        val result = dataSource.write(mockProject)

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `write should return false when not acknowledged`() {
        // Given
        val mockInsertOneResult = mockk<InsertOneResult>()
        every { mockInsertOneResult.wasAcknowledged() } returns false
        coEvery { mockCollection.insertOne(any<Project>(), any()) } returns mockInsertOneResult

        // When
        val result = dataSource.write(mockProject)

        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `writeAll should return true when acknowledged`() {
        // Given
        val mockInsertManyResult = mockk<InsertManyResult>()
        every { mockInsertManyResult.wasAcknowledged() } returns true
        coEvery { mockCollection.insertMany(any<List<Project>>(), any()) } returns mockInsertManyResult

        // When
        val result = dataSource.writeAll(mockProjects)

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `writeAll should return false when not acknowledged`() {
        // Given
        val mockInsertManyResult = mockk<InsertManyResult>()
        every { mockInsertManyResult.wasAcknowledged() } returns false
        coEvery { mockCollection.insertMany(any<List<Project>>(), any()) } returns mockInsertManyResult

        // When
        val result = dataSource.writeAll(mockProjects)

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