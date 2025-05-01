package com.berlin.logic.generateIdHelper

import kotlin.random.Random

interface IdGenerator {
    fun generateId(
        input: String,
        padChar: Char = input[input.length / 2],
        padCharLength: Int  = input.length + Random.Default.nextInt(1, 5)
        ): String
}