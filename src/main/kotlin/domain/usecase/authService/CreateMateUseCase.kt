package com.berlin.domain.usecase.authService
import com.berlin.domain.exception.InvalidCredentialsException
import com.berlin.domain.hashPassword.HashingString
import com.berlin.domain.usecase.utils.IDGenerator.IdGenerator
import com.berlin.domain.model.User
import com.berlin.domain.model.UserRole
import com.berlin.domain.repository.AuthenticationRepository

class CreateMateUseCase(
    private val repository: AuthenticationRepository,
    private val idGenerator: IdGenerator,
    private val hashingString: HashingString
) {
    fun createMate(userName: String, password: String): Result<User> {
        if (userName.isEmpty() || password.isEmpty()) {
            return Result.failure(InvalidCredentialsException("Username and password must not be empty"))
        }
        if (password.length < MAIN_PASSWORD_LENGHT) {
            return Result.failure(InvalidCredentialsException("Password less than 8 characters"))
        }

        val hashedPassword=hashingString.hashPassword(password)
        val newUser = User(
            id = idGenerator.generateId(password),
            userName = userName,
            password = hashedPassword,
            role = UserRole.MATE
        )
        return repository.createMate(newUser)
    }
     private companion object{
       const val MAIN_PASSWORD_LENGHT = 8
    }
}
