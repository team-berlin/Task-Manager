package com.berlin.domain.repository

interface AuthenticationRepository {
    fun login(userName: String, password: String): Result<com.berlin.domain.model.User>
    fun createMate(userName: String, password: String): Result<com.berlin.domain.model.User>
    fun getUserById(userId: String): com.berlin.domain.model.User?
    fun getAllUsers(): List<com.berlin.domain.model.User>
    fun getCurrentUser(): com.berlin.domain.model.User?

}
