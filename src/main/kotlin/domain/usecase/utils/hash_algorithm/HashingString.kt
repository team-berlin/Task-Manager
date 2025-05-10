package com.berlin.domain.usecase.utils.hash_algorithm

interface HashingString {
    fun hashPassword(password: String):String
}