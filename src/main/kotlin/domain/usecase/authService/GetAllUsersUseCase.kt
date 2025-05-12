package com.berlin.domain.usecase.authService
import com.berlin.domain.model.user.User
import com.berlin.domain.repository.AuthenticationRepository

class GetAllUsersUseCase(
    private val repository: AuthenticationRepository
) {
    fun getAllUsers(): List<User> {
        return repository.getAllUsers()
    }
}