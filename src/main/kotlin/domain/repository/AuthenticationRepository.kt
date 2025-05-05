package com.berlin.domain.repository

import com.berlin.domain.model.User


interface AuthenticationRepository {
    fun login(userName: String, password: String): Result<User>
    fun createMate(user: User): Result<User>
    fun getUserById(userId: String): Result<User>
    fun getAllUsers(): Result<List<User>>
    fun getCurrentUser(): Result<User>

}