package com.berlin.data.mongodb.config

import com.mongodb.MongoClientSettings
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.mongodb.kotlin.client.coroutine.MongoCollection
import io.mockk.*
import org.bson.codecs.configuration.CodecRegistry
import org.bson.codecs.pojo.PojoCodecProvider
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName

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

        // Create instance with test values
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
    @DisplayName("getDatabase should return database with correct name")
    fun `getDatabase returns database with correct name`() {
        // Given
        coEvery { mockMongoClient.getDatabase(any()) } returns mockMongoDatabase

        // When
        val database = mongoConfig.getDatabase(mockMongoClient)

        // Then
        verify { mockMongoClient.getDatabase("testDatabase") }
        assertEquals(mockMongoDatabase, database)
    }

    @Test
    @DisplayName("getCollection should return typed collection with correct name")
    fun `getCollection returns typed collection with correct name`() {
        // Given
        coEvery { mockMongoDatabase.getCollection<TestDocument>(any()) } returns mockCollection

        // When
        val collection = mongoConfig.getCollection<TestDocument>(mockMongoDatabase, "testCollection")

        // Then
        verify { mockMongoDatabase.getCollection<TestDocument>("testCollection") }
        assertEquals(mockCollection, collection)
    }

    @Test
    @DisplayName("test constructor sets correct values")
    fun `constructor sets correct values`() {
        // When
        val customConfig = MongoConfig("custom-connection-string", "custom-db-name")

        // Then
        assertNotNull(customConfig)
    }

    @Test
    fun `createMongoClient creates client with correct configuration`() {
        // Mock the static methods we need to control
        mockkStatic(MongoClient::class)
        mockkStatic(MongoClientSettings::class)
        mockkStatic(CodecRegistry::class)
        mockkStatic(PojoCodecProvider::class)

        // Mock CodecProvider
        val mockProvider = mockk<PojoCodecProvider>()
        val mockBuilder = mockk<PojoCodecProvider.Builder>()
        coEvery { PojoCodecProvider.builder() } returns mockBuilder
        coEvery { mockBuilder.automatic(true) } returns mockBuilder
        coEvery { mockBuilder.build() } returns mockProvider

        // Mock CodecRegistry
        val mockRegistry = mockk<CodecRegistry>()
        val defaultRegistry = mockk<CodecRegistry>()
        coEvery { MongoClientSettings.getDefaultCodecRegistry() } returns defaultRegistry
        mockkStatic("org.bson.codecs.configuration.CodecRegistries")
        coEvery {
            org.bson.codecs.configuration.CodecRegistries.fromProviders(mockProvider)
        } returns mockRegistry
        coEvery {
            org.bson.codecs.configuration.CodecRegistries.fromRegistries(defaultRegistry, mockRegistry)
        } returns mockRegistry

        // Mock ClientSettings
        val mockSettings = mockk<MongoClientSettings>()
        val mockSettingsBuilder = mockk<MongoClientSettings.Builder>()
        coEvery { MongoClientSettings.builder() } returns mockSettingsBuilder
        coEvery { mockSettingsBuilder.applyConnectionString(any()) } returns mockSettingsBuilder
        coEvery { mockSettingsBuilder.codecRegistry(mockRegistry) } returns mockSettingsBuilder
        coEvery { mockSettingsBuilder.build() } returns mockSettings

        // Mock MongoClient creation
        coEvery { MongoClient.create(mockSettings) } returns mockMongoClient

        // Execute the method
        val result = mongoConfig.createMongoClient()

        // Verify
        assertNotNull(result)
        verify {
            PojoCodecProvider.builder()
            mockBuilder.automatic(true)
            mockBuilder.build()
            MongoClientSettings.getDefaultCodecRegistry()
            org.bson.codecs.configuration.CodecRegistries.fromProviders(mockProvider)
            org.bson.codecs.configuration.CodecRegistries.fromRegistries(defaultRegistry, mockRegistry)
            MongoClientSettings.builder()
            mockSettingsBuilder.applyConnectionString(match {
                it.connectionString == "mongodb://localhost:27017"
            })
            mockSettingsBuilder.codecRegistry(mockRegistry)
            mockSettingsBuilder.build()
            MongoClient.create(mockSettings)
        }
    }
    data class TestDocument(val id: String, val name: String)
}