package com.berlin.data.authentication

import com.berlin.data.BaseDataSource
import com.berlin.domain.exception.UserNotFoundException
import com.berlin.domain.exception.UserNotLoggedInException
import com.berlin.domain.hashPassword.HashingPassword
import com.berlin.domain.hashPassword.MD5Hasher
import com.berlin.domain.helper.IdGenerator
import com.berlin.domain.helper.IdGeneratorImplementation
import com.berlin.domain.model.User
import com.berlin.domain.model.UserRole
import com.berlin.domain.repository.AuthenticationRepository
import data.UserCache
import kotlin.Result.Companion.failure


class AuthenticationRepositoryImpl(
    private val userDataSource: BaseDataSource<User>
    ): AuthenticationRepository {

    override suspend fun login(userName: String, password: String): Result<User> {
        val user = userDataSource.getAll().find { it.userName == userName && it.password == password }
        return if (user != null) {
            Result.success(user)
        } else {
            failure(Exception("Invalid credentials"))
        }
    }

    override suspend fun createMate(user: User): Result<User> {
        userDataSource.write(user)
        return Result.success(user)

    }

    override suspend fun getUserById(userId: String): Result<User> =
        userDataSource.getById(userId)
            ?.let(Result.Companion::success)
            ?: failure(UserNotFoundException(userId))


    override suspend fun getAllUsers(): Result<List<User>> {
       return userDataSource.getAll().let(Result.Companion::success)
    }


    override suspend fun getCurrentUser(): Result<User> {
        val user = UserCache.currentUser
        return if (user != null) {
            Result.success(user)
        } else {
            Result.failure(UserNotLoggedInException("No one logged in"))

        }

    }

}