package com.berlin.domain.fakeData

import com.berlin.domain.usecase.utils.hash_algorithm.HashingString

class FakeHashingString : HashingString {
    override fun hashPassword(password: String): String = "$password-hashed"

}
