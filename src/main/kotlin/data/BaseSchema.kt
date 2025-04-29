package com.berlin.data

interface BaseSchema<T> {
    val fileName: String
    val header: List<String>

    fun toRow(entity: T): List<String>
    fun fromRow(row: List<String>): T?
    fun getId(entity: T): String
}