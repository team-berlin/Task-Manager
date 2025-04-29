package com.berlin

import kotlin.random.Random

fun String.generateId(): String =
    this.trim()
        .replace(" ", "")
        .ifEmpty { throw IllegalArgumentException("String must not be empty") }
        .padEnd(this.length + Random.nextInt(1, 5),
            this[this.length / 2])
        .let { paddedNumber ->
            "${paddedNumber}_${System.currentTimeMillis() % 100000}"
        }
fun main() {
    println("hello".generateId())
}