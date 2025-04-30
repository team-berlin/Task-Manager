package com.berlin.logic.usecase.authService

import com.berlin.logic.repositories.AuthenticationRepository
import com.berlin.model.User

class GetUserByIDUseCase(
    private val repository: AuthenticationRepository
) {
    fun getUserById(id:String): User? {
        if (id.isEmpty())
            throw IllegalArgumentException("User ID must not be blank.")

        return repository.getUserById(id)
    }
}