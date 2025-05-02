package com.berlin.domain.usecase.authService
import com.berlin.domain.repository.AuthenticationRepository

class FetchAllUsersUseCase(
    private val repository: AuthenticationRepository
) {
    fun getAllUsers(): List<com.berlin.domain.model.User> {
        return repository.getAllUsers()
    }
}