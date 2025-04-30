package com.berlin.logic.usecase

import com.berlin.logic.repositories.AuthenticationRepository
import com.berlin.model.User

class AuthenticationService: AuthenticationRepository {
    override fun login(userName: String, password: String): User? {
        TODO("Not yet implemented")
    }

    override fun createMate(user: User): Boolean {
        TODO("Not yet implemented")
    }

    override fun getUserById(userId: String): User? {
        TODO("Not yet implemented")
    }

    override fun getAllUsers(): List<User> {
        TODO("Not yet implemented")
    }

    override fun getCurrentUser(): List<User>? {
        TODO("Not yet implemented")
    }
}