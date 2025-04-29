package com.berlin.data

interface BaseRepository<T> {
    fun write(row:T):Boolean
    fun writeAll(rows:List<T>):Boolean
    fun readAll():List<T>
    fun update(id:String,row: T):Boolean
    fun deleteById(id: String):Boolean
}