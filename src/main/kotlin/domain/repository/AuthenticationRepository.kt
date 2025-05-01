package com.berlin.domain.repository

import com.berlin.model.User

interface AuthenticationRepository {
    fun login(userName: String, password: String): Result<User>
    fun createMate(userName: String, password: String): Result<User>
    fun getUserById(userId: String): User?
    fun getAllUsers(): List<User>
    fun getCurrentUser(): User?

}
