package com.berlin.domain.usecase.utils.id_generator

import kotlin.random.Random

interface IdGenerator {
    fun generateId(
        input: String,
        padChar: Char = input[input.length / 2],
        padCharLength: Int  = input.length + Random.nextInt(1, 5)
    ): String
}