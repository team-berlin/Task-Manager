package com.berlin.logic.generateIdHelper

class DefaultIdGenerator : IdGenerator {
    override fun generateId(
        input: String, padChar: Char, padCharLength: Int): String {
        input.trim()
            .replace(" ", "")
            .ifEmpty { throw IllegalArgumentException("String must not be empty") }
            .padEnd(padCharLength, padChar).let { paddedNumber ->
                "${paddedNumber}_${System.currentTimeMillis() % 100000}"
            }
        return input
    }
}