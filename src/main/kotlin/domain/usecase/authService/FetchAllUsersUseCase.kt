package com.berlin.domain.usecase.authService
import com.berlin.domain.repository.AuthenticationRepository
import com.berlin.model.User

class FetchAllUsersUseCase(
    private val repository: AuthenticationRepository
) {
    fun getAllUsers():List<User>{
        return repository.getAllUsers()
    }
}