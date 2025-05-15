package com.berlin.data.mongodb.datasource

import com.berlin.data.dto.UserDto
import com.berlin.data.mongodb.config.MongoConfig
import com.berlin.domain.model.user.User
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
class MongoDBUserDataSourceTest {

    private lateinit var dataSource: MongoDBUserDataSource
    private val mockMongoConfig = mockk<MongoConfig>()
    private val mockCollection = mockk<com.mongodb.kotlin.client.coroutine.MongoCollection<UserDto>>()
    private val mockMongoClient = mockk<com.mongodb.kotlin.client.coroutine.MongoClient>()
    private val mockMongoDatabase = mockk<com.mongodb.kotlin.client.coroutine.MongoDatabase>()
    private val mockFindPublisher = mockk<com.mongodb.kotlin.client.coroutine.FindFlow<UserDto>>()

    private val mockUser = UserDto(
        id = "user1",
        userName = "admin",
        password = "secure123",
        role = User.UserRole.ADMIN
    )

    private val mockUsers = listOf(
        mockUser,
        UserDto(
            id = "user2",
            userName = "mate",
            password = "pass456",
            role = User.UserRole.MATE
        )
    )

    @BeforeEach
    fun setUp() {
        every { mockMongoConfig.createMongoClient() } returns mockMongoClient
        every { mockMongoConfig.getDatabase(mockMongoClient) } returns mockMongoDatabase
        every { mockMongoConfig.getCollection<UserDto>(mockMongoDatabase, "users") } returns mockCollection

        coEvery { mockFindPublisher.collect(any()) } coAnswers {
            val collector = arg<FlowCollector<UserDto>>(0)
            collector.emit(mockUser)
        }

        dataSource = MongoDBUserDataSource(mockMongoConfig)
    }

    @Test
    fun `getAll should return list of users`() {
        // Given
        coEvery { mockCollection.find() } returns mockFindPublisher
        coEvery { mockFindPublisher.collect(any()) } coAnswers {
            val collector = arg<FlowCollector<UserDto>>(0)
            mockUsers.forEach { collector.emit(it) }
        }

        // When
        val result = dataSource.getAll()

        // Then
        assertThat(result).isEqualTo(mockUsers)
    }

    @Test
    fun `getById should return user when found`() {
        // Given
        coEvery { mockCollection.find(any<Bson>()) } returns mockFindPublisher
        coEvery { mockFindPublisher.collect(any()) } coAnswers {
            val collector = arg<FlowCollector<UserDto>>(0)
            collector.emit(mockUser)
        }

        // When
        val result = dataSource.getById("user1")

        // Then
        assertThat(result).isEqualTo(mockUser)
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
                any<UserDto>(),
                any()
            )
        } returns mockUpdateResult

        // When
        val result = dataSource.update("user1", mockUser)

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `update should return false when not acknowledged`() {
        // Given
        val mockUpdateResult = mockk<UpdateResult>()
        every { mockUpdateResult.wasAcknowledged() } returns false
        coEvery { mockCollection.replaceOne(any<Bson>(), any<UserDto>(), any()) } returns mockUpdateResult

        // When
        val result = dataSource.update("user1", mockUser)

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
        val result = dataSource.delete("user1")

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
        val result = dataSource.delete("user1")

        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `write should return true when acknowledged`() {
        // Given
        val mockInsertOneResult = mockk<InsertOneResult>()
        every { mockInsertOneResult.wasAcknowledged() } returns true
        coEvery { mockCollection.insertOne(any<UserDto>(), any()) } returns mockInsertOneResult

        // When
        val result = dataSource.write(mockUser)

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `write should return false when not acknowledged`() {
        // Given
        val mockInsertOneResult = mockk<InsertOneResult>()
        every { mockInsertOneResult.wasAcknowledged() } returns false
        coEvery { mockCollection.insertOne(any<UserDto>(), any()) } returns mockInsertOneResult

        // When
        val result = dataSource.write(mockUser)

        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `writeAll should return true when acknowledged`() {
        // Given
        val mockInsertManyResult = mockk<InsertManyResult>()
        every { mockInsertManyResult.wasAcknowledged() } returns true
        coEvery { mockCollection.insertMany(any<List<UserDto>>(), any()) } returns mockInsertManyResult

        // When
        val result = dataSource.writeAll(mockUsers)

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `writeAll should return false when not acknowledged`() {
        // Given
        val mockInsertManyResult = mockk<InsertManyResult>()
        every { mockInsertManyResult.wasAcknowledged() } returns false
        coEvery { mockCollection.insertMany(any<List<UserDto>>(), any()) } returns mockInsertManyResult

        // When
        val result = dataSource.writeAll(mockUsers)

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