package com.berlin.logic.usecase

import com.berlin.logic.repositories.AuthenticationRepository

class GettingUsersLoggedInUseCase(
    private val repository: AuthenticationRepository
) {
}