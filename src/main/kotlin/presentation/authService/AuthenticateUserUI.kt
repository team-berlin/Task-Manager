package com.berlin.presentation.authService

import com.berlin.presentation.UiRunner
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import domain.usecase.authService.AuthenticateUserUseCase

class AuthenticateUserUi(
    private val authenticateUser: AuthenticateUserUseCase,
    private val viewer: Viewer,
    private val reader: Reader
): UiRunner {
    override val id: Int = 1
    override val label: String = "log in"
    override fun run() {
        authenticateLoop()
    }

    private fun validateUser(): Result<com.berlin.domain.model.User> {
        viewer.show("Enter your user name: ")
        val userName = reader.read()?.trim().orEmpty()
        viewer.show("Enter your password: ")
        val password = reader.read()?.trim().orEmpty()
         return authenticateUser.login(userName, password)
    }

    private tailrec fun authenticateLoop() {
        validateUser().fold(
            onSuccess = {
                viewer.show("Welcome ${it.userName}")
            },
            onFailure = {
                viewer.show("try again")
                authenticateLoop()
            }
        )
    }

}