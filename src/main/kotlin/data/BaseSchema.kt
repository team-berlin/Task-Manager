package com.berlin.data

interface BaseSchema<T> {
    val fileName: String
    val header: List<String>

    suspend fun toRow(entity: T): List<String>
    suspend fun fromRow(row: List<String>): T?
    suspend fun getId(entity: T): String?
}