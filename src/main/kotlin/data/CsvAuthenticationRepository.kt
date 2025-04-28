package com.berlin.data

import com.berlin.logic.repositories.AuthenticationRepository
import com.berlin.model.User

class CsvAuthenticationRepository:AuthenticationRepository {
    override fun createUser(user: User): Boolean {
        return false
    }

    override fun getUserById(userId: String): User? {
        return null
    }

    override fun getAllUsers(): List<User> {
        return emptyList()
    }
}