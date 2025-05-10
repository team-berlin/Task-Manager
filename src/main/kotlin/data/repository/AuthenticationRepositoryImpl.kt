package com.berlin.data.repository

import com.berlin.data.BaseDataSource
import com.berlin.domain.exception.UserNotFoundException
import com.berlin.domain.model.User
import com.berlin.domain.repository.AuthenticationRepository
import data.UserCache
import kotlin.Result.Companion.failure


class AuthenticationRepositoryImpl(
    private val userCache: UserCache,
    private val userDataSource: BaseDataSource<User>
    ): AuthenticationRepository {

    override fun login(userName: String, password: String): Result<User> {
        val user = userDataSource.getAll().find { it.userName == userName && it.password == password }
        return if (user != null) {
            Result.success(user)
        } else {
            failure(Exception("Invalid credentials"))
        }
    }

    override fun createMate(user: User): Result<User> {
        userDataSource.write(user)
        return Result.success(user)

    }

    override fun getUserById(userId: String): Result<User> =
        userDataSource.getById(userId)
            ?.let(Result.Companion::success)
            ?: failure(UserNotFoundException(userId))


    override fun getAllUsers(): Result<List<User>> {
       return userDataSource.getAll().let(Result.Companion::success)
    }


    override fun getCurrentUser(): Result<User> {
        val user = userCache.currentUser
        return Result.success(user)

    }

}