package com.berlin.logic.usecase

import com.berlin.data.UserCache
import com.berlin.logic.InvalidCredentialsException
import com.berlin.logic.repositories.AuthenticationRepository
import com.berlin.model.User

class AuthenticateUserUseCase(
    private val repository: AuthenticationRepository
) {
    fun login(userName: String, password: String): Result<User> {
        if (userName.isEmpty() || password.isEmpty()) {
            return Result.failure(InvalidCredentialsException("No user found"))
        }

        if (repository.getAllUsers().isEmpty()) {
            return Result.failure(InvalidCredentialsException("List is empty"))
        }

        val cachedUser = UserCache.currentUser
        if (cachedUser != null && cachedUser.userName == userName) {
            return Result.success(cachedUser)
        }

        val userResult = repository.login(userName, password)

        return userResult.fold(
            onSuccess = { user ->
                UserCache.currentUser = user
                Result.success(user)
            },
            onFailure = { exception ->
                Result.failure(exception)
            }
        )
    }
}
