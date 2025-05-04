package com.berlin.domain.usecase.authService

import com.berlin.domain.model.User
import com.berlin.domain.repository.AuthenticationRepository

class GetUserByIDUseCase(
    private val repository: AuthenticationRepository,
) {
    fun getUserById(id: String): Result<User> {
        if (!isIDValid(id))
            throw IndexOutOfBoundsException("User ID can't be empty or just digits")
        return repository.getUserById(id)

    }

    private fun isIDValid(id: String): Boolean =
        id.isNotBlank() && !id.all { it.isDigit() }
}