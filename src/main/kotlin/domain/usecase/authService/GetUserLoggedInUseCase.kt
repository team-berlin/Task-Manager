package com.berlin.domain.usecase.authService
import com.berlin.domain.model.User
import com.berlin.domain.repository.AuthenticationRepository
class GetUserLoggedInUseCase(
    private val repository: AuthenticationRepository
) {
    fun getCurrentUser(): Result<User>{

        return repository.getCurrentUser()
    }
}