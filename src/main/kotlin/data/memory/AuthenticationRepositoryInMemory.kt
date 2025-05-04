package com.berlin.data.memory

import com.berlin.data.DummyData.users
import com.berlin.domain.exception.UserNotFoundException
import com.berlin.domain.exception.UserNotLoggedInException
import com.berlin.domain.hashPassword.HashingPassword
import com.berlin.domain.hashPassword.MD5Hasher
import com.berlin.domain.helper.IdGenerator
import com.berlin.domain.helper.IdGeneratorImplementation
import com.berlin.domain.model.User
import com.berlin.domain.model.UserRole
import com.berlin.domain.permission.assignPermissions
import com.berlin.domain.repository.AuthenticationRepository
import data.UserCache
import kotlin.Result.Companion.failure

class AuthRepositoryInMemory : AuthenticationRepository {
    private val userId: IdGenerator = IdGeneratorImplementation()
    private val hashingPassword: HashingPassword = MD5Hasher()

    override fun login(userName: String, password: String): Result<User> {
        val hashPassword = hashingPassword.hashPassword(password)
        val user = users.find { it.userName == userName && it.password == hashPassword }
        return if (user != null) {
            Result.success(user)
        } else {
            failure(Exception("Invalid credentials"))
        }
    }

    override fun createMate(userName: String, password: String): Result<User> {
        val checkUserName = users.find { it.userName == userName }
        if (checkUserName != null) {
            return failure(Exception("user name is already exist"))
        } else {
            val hashingPassword = hashingPassword.hashPassword(password)
            val newUser = User(
                id = userId.generateId(userName),
                userName = userName,
                password = hashingPassword,
                permission = assignPermissions(UserRole.MATE),
                role = UserRole.MATE
            )
            users.add(newUser)
            return Result.success(newUser)
        }

    }

    override fun getUserById(userId: String): Result<User> =
        users.firstOrNull { it.id == userId }
            ?.let(Result.Companion::success)
            ?: failure(UserNotFoundException(userId))


    override fun getAllUsers(): Result<List<User>> = users.let(Result.Companion::success)


    override fun getCurrentUser(): Result<User> {
        val user = UserCache.currentUser
        return if (user != null) {
            Result.success(user)
        } else {
            Result.failure(UserNotLoggedInException("No one logged in"))
        }
    }

}
