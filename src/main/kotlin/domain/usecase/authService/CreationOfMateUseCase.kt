package com.berlin.domain.usecase.authService
import com.berlin.domain.exception.InvalidCredentialsException
import com.berlin.domain.hashPassword.HashingPassword
import com.berlin.domain.model.User
import com.berlin.domain.repository.AuthenticationRepository

class CreationOfMateUseCase(
    private val repository: AuthenticationRepository,
    private val hashingPassword: HashingPassword
) {
    fun createMate(userName: String, password: String): Result<User> {
        if (userName.isEmpty() || password.isEmpty()) {
            return Result.failure(InvalidCredentialsException("Username and password must not be empty"))
        }
        if (password.length < MAIN_PASSWORD_LENGHT) {
            return Result.failure(InvalidCredentialsException("Password less than 8 characters"))
        }

        val hashedPassword=hashingPassword.hashPassword(password)
        return repository.createMate(userName, hashedPassword)
    }
     private companion object{
       const val MAIN_PASSWORD_LENGHT = 8
    }
}
