package com.berlin.logic.usecase.authService

import com.berlin.logic.repositories.AuthenticationRepository
import com.berlin.model.User

class GettingUsersLoggedInUseCase(
    private val repository: AuthenticationRepository
) {
    fun getCurrentUser(): User?{
        return repository.getCurrentUser()
    }
}