package com.berlin.data.mongodb.config

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.pojo.PojoCodecProvider
import com.mongodb.kotlin.client.coroutine.MongoCollection

class MongoConfig(
    private val connectionString: String = "mongodb+srv://diyarHussein:7p53.t@GQ4F#@2c@planmate.gzyncow.mongodb.net/?retryWrites=true&w=majority&appName=PlanMate",
    private val databaseName: String = "PlanMate"
) {

    fun createMongoClient(): MongoClient {
        val pojoCodecProvider = PojoCodecProvider.builder()
            .automatic(true)
            .build()

        val codecRegistry = CodecRegistries.fromRegistries(
            MongoClientSettings.getDefaultCodecRegistry(),
            CodecRegistries.fromProviders(pojoCodecProvider)
        )

        val clientSettings = MongoClientSettings.builder()
            .applyConnectionString(ConnectionString(connectionString))
            .codecRegistry(codecRegistry)
            .build()

        return MongoClient.create(clientSettings)
    }

    fun getDatabase(client: MongoClient): MongoDatabase {
        return client.getDatabase(databaseName)
    }

    inline fun <reified T : Any> getCollection(database: MongoDatabase, collectionName: String): MongoCollection<T> {
        return database.getCollection<T>(collectionName)
    }
}