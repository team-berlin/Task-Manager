package com.berlin.domain.usecase.authService
import com.berlin.domain.exception.InvalidCredentialsException
import com.berlin.domain.usecase.utils.hash_algorithm.HashingString
import com.berlin.domain.usecase.utils.id_generator.IdGenerator
import com.berlin.domain.model.User
import com.berlin.domain.model.UserRole
import com.berlin.domain.repository.AuthenticationRepository

class CreateMateUseCase(
    private val repository: AuthenticationRepository,
    private val idGenerator: IdGenerator,
    private val hashingString: HashingString
) {
    fun createMate(userName: String, password: String): User {
        if (userName.isEmpty() || password.isEmpty()) {
            throw InvalidCredentialsException("Username and password must not be empty")
        }
        if (password.length < MAIN_PASSWORD_LENGTH) {
            throw InvalidCredentialsException("Password less than 8 characters")
        }

        val hashedPassword=hashingString.hashPassword(password)
        val newUser = User(
            id = idGenerator.generateId(userName),
            userName = userName,
            role = UserRole.MATE
        )
        return repository.createMate(newUser, hashedPassword)
    }
     private companion object{
       const val MAIN_PASSWORD_LENGTH = 8
    }
}
