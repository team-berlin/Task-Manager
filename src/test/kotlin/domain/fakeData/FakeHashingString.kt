package com.berlin.domain.fakeData

import com.berlin.domain.hashPassword.HashingString

class FakeHashingString : HashingString {
    override fun hashPassword(password: String): String = "$password-hashed"

}
