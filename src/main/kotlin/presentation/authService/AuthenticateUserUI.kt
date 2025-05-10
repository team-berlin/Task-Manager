package com.berlin.presentation.authService

import com.berlin.domain.model.User
import com.berlin.presentation.UiRunner
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import data.UserCache
import domain.usecase.authService.AuthenticateUserUseCase

class AuthenticateUserUi(
    private val authenticateUser: AuthenticateUserUseCase,
    private val viewer: Viewer,
    private val reader: Reader
) : UiRunner {
    override val id: Int = 1
    override val label: String = "Log in"

    override suspend fun run() {
        authenticateLoop()
    }

    private suspend fun validateUser(): Result<User> {
        viewer.show("Enter your user name: ")
        val userName = reader.read()?.trim().orEmpty()
        viewer.show("Enter your password: ")
        val password = reader.read()?.trim().orEmpty()
        return authenticateUser.login(userName, password)
    }

    private suspend fun authenticateLoop(failedAttemps: Int = 0, maxAttemps: Int = 3) {
        validateUser().fold(
            onSuccess = {
                viewer.show("Welcome ${it.userName}")
                UserCache.currentUser = it
            },
            onFailure = {
                viewer.show("Try again")
                if (failedAttemps < maxAttemps) {
                    authenticateLoop(failedAttemps + 1, maxAttemps)
                }
            }
        )
    }
}
