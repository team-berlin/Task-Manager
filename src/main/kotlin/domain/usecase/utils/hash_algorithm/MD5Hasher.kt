package com.berlin.domain.usecase.utils.hash_algorithm

import java.security.MessageDigest

class MD5Hasher : HashingString {
    override fun hashPassword(password: String): String {
        return encode(password)
    }
    private fun encode(password: String):String{
        val bytes = MessageDigest.getInstance("MD5").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}