package com.berlin.domain.usecase.authService
import com.berlin.domain.model.user.User
import com.berlin.domain.repository.AuthenticationRepository

class GetUserLoggedInUseCase(
    private val repository: AuthenticationRepository
) {
    operator fun invoke(): User {
        return repository.getCurrentUser()
    }
}