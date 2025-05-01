package domain.helper

interface HashingPassword {
    fun hashPassword(password: String):String
}