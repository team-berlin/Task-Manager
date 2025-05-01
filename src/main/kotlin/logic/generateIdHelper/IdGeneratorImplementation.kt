package com.berlin.logic.generateIdHelper

class IdGeneratorImplementation : IdGenerator {
    override fun generateId(
        prefix: String, padChar: Char, padCharLength: Int): String {
        val cleanedInput = prefix.trim().replace(" ", "")
            .ifEmpty { throw IllegalArgumentException("String must not be empty") }

        val paddedNumber = cleanedInput.padEnd(padCharLength, padChar)

        return "${paddedNumber}_${System.currentTimeMillis() % 100000}"
    }
}