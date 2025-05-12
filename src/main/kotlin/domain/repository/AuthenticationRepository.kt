package com.berlin.domain.repository

import com.berlin.domain.model.User

interface AuthenticationRepository {
    fun login(userName: String, password: String):  User
    fun createMate(user: User, password: String): User
    fun getUserById(userId: String):  User
    fun getAllUsers():  List<User>
    fun getCurrentUser():  User
}