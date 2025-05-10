package com.berlin.domain.repository

import com.berlin.domain.model.User


interface AuthenticationRepository {
    suspend fun login(userName: String, password: String): Result<User>
    suspend fun createMate(user: User): Result<User>
    suspend fun getUserById(userId: String): Result<User>
    suspend fun getAllUsers(): Result<List<User>>
    suspend fun getCurrentUser(): Result<User>

}