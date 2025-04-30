package com.berlin.logic.usecase

import com.berlin.logic.repositories.AuthenticationRepository

class FetchAllUsersUseCase(
    private val repository: AuthenticationRepository
) {
}