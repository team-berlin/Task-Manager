package domain.fakeData

import domain.helper.HashingPassword

class FakeHashingPassword : HashingPassword {
    override fun hashPassword(password: String): String = "$password-hashed"

}
