package com.berlin.domain.usecase.authService
import com.berlin.domain.model.User
import com.berlin.domain.repository.AuthenticationRepository
class GettingUsersLoggedInUseCase(
    private val repository: AuthenticationRepository
) {
    fun getCurrentUser(): User?{
        return repository.getCurrentUser()
    }
}