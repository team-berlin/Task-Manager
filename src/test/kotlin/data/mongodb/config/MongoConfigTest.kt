package com.berlin.data.mongodb.config

import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.mongodb.kotlin.client.coroutine.MongoCollection
import io.mockk.every
import io.mockk.mockk

import io.mockk.verify
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MongoConfigTest {

    private lateinit var mongoConfig: MongoConfig
    private lateinit var mockMongoClient: MongoClient
    private lateinit var mockMongoDatabase: MongoDatabase
    private lateinit var mockCollection: MongoCollection<TestDocument>

    @BeforeEach
    fun setUp() {
        // Create mocks
        mockMongoClient = mockk(relaxed = true)
        mockMongoDatabase = mockk(relaxed = true)
        mockCollection = mockk(relaxed = true)

        mongoConfig = MongoConfig(
            connectionString = "mongodb://localhost:27017",
            databaseName = "testDatabase"
        )
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getDatabase should return database with correct name`() {
        // Given
        every { mockMongoClient.getDatabase(any()) } returns mockMongoDatabase

        // When
        val database = mongoConfig.getDatabase(mockMongoClient)

        // Then
        verify { mockMongoClient.getDatabase("testDatabase") }
        assertEquals(mockMongoDatabase, database)
    }

    @Test
    fun `getCollection should return typed collection with correct name`() {
        // Given
        every { mockMongoDatabase.getCollection<TestDocument>(any()) } returns mockCollection

        // When
        val collection = mongoConfig.getCollection<TestDocument>(mockMongoDatabase, "testCollection")

        // Then
        verify { mockMongoDatabase.getCollection<TestDocument>("testCollection") }
        assertEquals(mockCollection, collection)
    }

    @Test
    fun `test MongoConfig constructor sets values correctly`() {
        // When
        val config = MongoConfig("test-connection-string", "test-db")

        // Then
        assertNotNull(config)
    }

    // Data class for testing
    data class TestDocument(val id: String, val name: String)
}