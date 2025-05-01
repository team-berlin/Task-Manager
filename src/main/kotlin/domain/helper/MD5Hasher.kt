package domain.helper

import java.security.MessageDigest

class MD5Hasher : HashingPassword {
    override fun hashPassword(password: String): String {
        return encode(password)
    }
    private fun encode(password: String):String{
        val bytes = MessageDigest.getInstance("MD5").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}