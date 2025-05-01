package com.berlin.fakeData

import logic.hashPassword.HashingPassword

class FakeHashingPassword : HashingPassword {
    override fun hashPassword(password: String): String = "$password-hashed"

}
