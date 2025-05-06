package com.berlin.domain.hashPassword

interface HashingPassword {
    fun hashPassword(password: String):String
}