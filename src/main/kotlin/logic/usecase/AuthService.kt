package com.berlin.logic.usecase

import com.berlin.logic.repositories.AuthenticationRepository
import com.berlin.model.User

class AuthService: AuthenticationRepository {
    override fun login(userName: String, password: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun createUser(user: User): Boolean {
        TODO("Not yet implemented")
    }

    override fun getUserById(userId: String): User? {
        TODO("Not yet implemented")
    }

    override fun getAllUsers(): List<User> {
        TODO("Not yet implemented")
    }
    companion object{
        val PASSWORD_VALID_NUMBER = 8
    }
}