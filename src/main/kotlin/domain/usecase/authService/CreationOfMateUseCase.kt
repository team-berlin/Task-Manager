package com.berlin.domain.usecase.authService
import com.berlin.domain.exception.InvalidCredentialsException
import com.berlin.domain.hashPassword.HashingPassword
import com.berlin.domain.repository.AuthenticationRepository
import com.berlin.model.User

class CreationOfMateUseCase(
    private val repository: AuthenticationRepository,
    private val hashingPassword: HashingPassword
) {
    fun createMate(userName: String, password: String): Result<com.berlin.domain.model.User> {
        if (userName.isEmpty() || password.isEmpty()) {
            return Result.failure(InvalidCredentialsException("Username and password must not be empty"))
        }
        if (password.length < 8) {
            return Result.failure(InvalidCredentialsException("Password less than 8 characters"))
        }
        if (repository.getAllUsers().any { it.userName == userName }) {
            return Result.failure(InvalidCredentialsException("Username already exists"))
        }
        val hashedPassword = hashingPassword.hashPassword(password)
        return repository.createMate(userName, hashedPassword)
    }
}
