package com.berlin.domain.usecase.authService
import com.berlin.domain.model.User
import com.berlin.domain.repository.AuthenticationRepository

class FetchAllUsersUseCase(
    private val repository: AuthenticationRepository
) {
    fun getAllUsers(): Result<List<User>> {
        return repository.getAllUsers()
    }
}