package com.berlin.logic.usecase

import com.berlin.logic.repositories.AuthenticationRepository
import com.berlin.model.User

class GettingUsersLoggedInUseCase(
    private val repository: AuthenticationRepository
) {
    fun getCurrentUser():List<User>?{
        return repository.getCurrentUser()
    }
}