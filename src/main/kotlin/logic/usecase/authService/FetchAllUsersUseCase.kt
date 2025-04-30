package com.berlin.logic.usecase.authService

import com.berlin.logic.repositories.AuthenticationRepository
import com.berlin.model.User

class FetchAllUsersUseCase(
    private val repository: AuthenticationRepository
) {
    fun getAllUsers():List<User>{
        return repository.getAllUsers()
    }
}