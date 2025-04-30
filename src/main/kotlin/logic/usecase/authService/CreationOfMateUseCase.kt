package com.berlin.logic.usecase.authService

import com.berlin.logic.InvalidCredentialsException
import com.berlin.logic.repositories.AuthenticationRepository
import com.berlin.model.User

class CreationOfMateUseCase(
    private val repository: AuthenticationRepository
) {
    fun createMate(userName: String, password: String): Result<User> {
        if (userName.isEmpty() || password.isEmpty()) {
            return Result.failure(InvalidCredentialsException("Username and password must not be empty"))
        }
        if (password.length < 8) {
            return Result.failure(InvalidCredentialsException("Password less than 8 characters"))
        }
        if (repository.getAllUsers().any { it.userName == userName }) {
            return Result.failure(InvalidCredentialsException("Username already exists"))
        }

        return repository.createMate(userName, password)
    }
}
