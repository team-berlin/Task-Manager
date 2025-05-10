package com.berlin.domain.usecase.utils.id_generator

class IdGeneratorImplementation : IdGenerator {
    override fun generateId(
        input: String, padChar: Char, padCharLength: Int,
    ): String {
        return input.trim()
            .replace(" ", "")
            .ifEmpty { throw IllegalArgumentException("String must not be empty") }
            .padEnd(padCharLength, padChar).let { paddedNumber ->
                "${paddedNumber}_${System.currentTimeMillis() % 100000}"
            }
    }
}