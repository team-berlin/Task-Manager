package com.berlin.domain.repository

import com.berlin.domain.model.user.User
import com.berlin.domain.model.user.UserCreation

interface AuthenticationRepository {
    fun login(userName: String, password: String): User
    fun createMate(user: UserCreation): User
    fun getUserById(userId: String): User
    fun getAllUsers():  List<User>
    fun getCurrentUser(): User
}