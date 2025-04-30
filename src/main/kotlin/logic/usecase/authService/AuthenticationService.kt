package com.berlin.logic.usecase.authService

import com.berlin.logic.repositories.AuthenticationRepository
import com.berlin.model.User

class AuthenticationService: AuthenticationRepository {
    override fun login(userName: String, password: String): Result<User> {
        TODO("Not yet implemented")
    }
    override fun createMate(userName: String, password: String): Result<User> {
        TODO("Not yet implemented")
    }
    override fun getUserById(userId: String): User? {
        TODO("Not yet implemented")
    }

    override fun getAllUsers(): List<User> {
        TODO("Not yet implemented")
    }

    override fun getCurrentUser(): User? {
        TODO("Not yet implemented")
    }
}