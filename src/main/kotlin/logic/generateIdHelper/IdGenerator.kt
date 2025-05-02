package com.berlin.logic.generateIdHelper

import kotlin.random.Random

interface IdGenerator {
    fun generateId(
        prefix: String,
        padChar: Char = prefix[prefix.length / 2],
        padCharLength: Int  = prefix.length + Random.Default.nextInt(1, 5)
    ): String
}