package com.berlin.data

interface BaseDataSource<T> {

    suspend fun getAll(): List<T>

    suspend fun getById(id: String): T?

    suspend fun update(id: String, entity: T): Boolean

    suspend fun delete(id: String): Boolean

    suspend fun write(entity: T): Boolean

    suspend fun writeAll(entities: List<T>): Boolean

}