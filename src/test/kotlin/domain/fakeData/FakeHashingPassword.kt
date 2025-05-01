package com.berlin.domain.fakeData

import com.berlin.domain.hashPassword.HashingPassword

class FakeHashingPassword : HashingPassword {
    override fun hashPassword(password: String): String = "$password-hashed"

}
