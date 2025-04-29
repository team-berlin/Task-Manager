package com.berlin.logic.repositories

import com.berlin.model.User

interface AuthenticationRepository {
    fun login(userName: String, password: String): Boolean
    fun createUser(user:User):Boolean
    fun getUserById(userId:String):User?
    fun getAllUsers():List<User>
}