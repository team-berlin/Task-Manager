package com.berlin.domain.usecase.authService
import com.berlin.domain.repository.AuthenticationRepository
import com.berlin.model.User

class GettingUsersLoggedInUseCase(
    private val repository: AuthenticationRepository
) {
    fun getCurrentUser(): User?{
        return repository.getCurrentUser()
    }
}