package com.berlin.domain.usecase.authService
import com.berlin.domain.model.User
import com.berlin.domain.repository.AuthenticationRepository
class GettingUsersLoggedInUseCase(
    private val repository: AuthenticationRepository
) {
    suspend fun getCurrentUser(): Result<User>{

        return repository.getCurrentUser()
    }
}