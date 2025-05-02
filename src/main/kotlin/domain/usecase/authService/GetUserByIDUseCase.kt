package com.berlin.domain.usecase.authService

import com.berlin.domain.repository.AuthenticationRepository
import com.berlin.model.User

class GetUserByIDUseCase(
    private val repository: AuthenticationRepository
) {
    fun getUserById(id:String): com.berlin.domain.model.User? {
        if (id.isEmpty())
            throw IllegalArgumentException("User ID must not be blank.")

        return repository.getUserById(id)
    }
}