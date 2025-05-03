package com.berlin.data.memory

import com.berlin.data.DummyData.users
import com.berlin.domain.exception.UserNotFoundException
import com.berlin.domain.helper.IdGenerator
import com.berlin.domain.helper.IdGeneratorImplementation
import com.berlin.domain.model.User
import com.berlin.domain.model.UserRole
import com.berlin.domain.repository.AuthenticationRepository
import data.UserCache
import kotlin.Result.Companion.failure

class AuthRepositoryInMemory : AuthenticationRepository {
    private val userId: IdGenerator = IdGeneratorImplementation()

    override fun login(userName: String, password: String): Result<User> {
        val user = users.find { it.userName == userName && it.password == password }
        return if (user != null) {
            Result.success(user)
        } else {
            failure(Exception("Invalid credentials"))
        }
    }

    override fun createMate(userName: String, password: String): Result<User> {
        val newUser = User(
            id = userId.generateId(userName),
            userName = userName,
            password = password,
            role = UserRole.MATE
        )
        users.add(newUser)
        return Result.success(newUser)
    }

    override fun getUserById(userId: String): Result<User> =
        users.firstOrNull { it.id == userId }
            ?.let(Result.Companion::success)
            ?: failure(UserNotFoundException(userId))


    override fun getAllUsers(): List<User> {
        return users
    }

    override fun getCurrentUser(): User? {
        return UserCache.currentUser
    }
}