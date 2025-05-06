package com.berlin.data

interface BaseDataSource<T> {

     fun getAll(): List<T>

     fun getById(id: String): T?

     fun update(id: String, entity: T): Boolean

     fun delete(id: String): Boolean

     fun write(entity: T): Boolean

     fun writeAll(entities: List<T>): Boolean

}