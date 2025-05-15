package com.berlin.domain.usecase.authService

import com.berlin.domain.exception.InvalidUserIdException
import com.berlin.domain.model.user.User
import com.berlin.domain.repository.AuthenticationRepository
import com.berlin.domain.usecase.utils.isIDValid

class GetUserByIDUseCase(
    private val repository: AuthenticationRepository,
) {
    operator fun invoke(id: String): User {
        if (isIDValid(id).not())
            throw InvalidUserIdException("User ID can't be empty or just digits")
        return repository.getUserById(id)
    }

}