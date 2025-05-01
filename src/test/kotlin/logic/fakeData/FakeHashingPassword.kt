package logic.fakeData

import logic.helper.HashingPassword

class FakeHashingPassword : HashingPassword {
    override fun hashPassword(password: String): String = "$password-hashed"

}
