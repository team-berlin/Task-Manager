package com.berlin.data

import com.berlin.logic.repositories.AuthenticationRepository
import com.berlin.model.User
import com.berlin.model.UserRole

class AuthRepositoryImp : AuthenticationRepository {
    private val listOfUser = mutableListOf<User>()

    override fun login(userName: String, password: String): Result<User> {
        val user = listOfUser.find { it.userName == userName && it.password == password }
        return if (user != null) {
            Result.success(user)
        } else {
            Result.failure(Exception("Invalid credentials"))
        }
    }

    override fun createMate(userName: String, password: String): Result<User> {
        val newUser = User(
            id = (listOfUser.size + 1).toString(),
            userName = userName,
            password = password,
            role = UserRole.MATE
        )
        listOfUser.add(newUser)
        return Result.success(newUser)
    }

    override fun getUserById(userId: String): User? {
       if (userId.isEmpty()) return null
         val user = listOfUser.find { it.id == userId }
        return user
    }

    override fun getAllUsers(): List<User> {
       return listOfUser
    }

    override fun getCurrentUser(): User? {
       return UserCache.currentUser
    }


}
