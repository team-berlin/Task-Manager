package com.berlin.logic.repositories

import com.berlin.model.User

interface AuthenticationRepository {
    fun login(userName: String, password: String): User?
    fun createMate(user:User):Boolean
    fun getUserById(userId:String):User?
    fun getAllUsers():List<User>
    fun getCurrentUser(): List<User>?
}