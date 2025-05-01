package com.berlin.logic.generateIdHelper

class DefaultIdGenerator : IdGenerator {
    override fun generateId(
        input: String, padChar: Char, padCharLength: Int): String {
        val cleanedInput = input.trim().replace(" ", "")
            .ifEmpty { throw IllegalArgumentException("String must not be empty") }

        val paddedNumber = cleanedInput.padEnd(padCharLength, padChar)

        return "${paddedNumber}_${System.currentTimeMillis() % 100000}"
    }
}