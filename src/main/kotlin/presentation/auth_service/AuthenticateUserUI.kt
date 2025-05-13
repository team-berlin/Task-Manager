package com.berlin.presentation.authService

import com.berlin.domain.exception.InvalidCredentialsException
import com.berlin.domain.model.user.User
import com.berlin.presentation.UiRunner
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import domain.usecase.auth_service.LoginUserUseCase

class AuthenticateUserUI(
    private val authenticateUser: LoginUserUseCase,
    private val viewer: Viewer,
    private val reader: Reader
) : UiRunner {
    override val id: Int = 4000
    override val label: String = "Log in"

    override fun run() {
        authenticateLoop()
    }

    fun validateUser(): User {
        viewer.show("Enter your user name: ")
        val userName = reader.read()?.trim().orEmpty()
        viewer.show("Enter your password: ")
        val password = reader.read()?.trim().orEmpty()
        return authenticateUser(userName, password)
    }

    private fun authenticateLoop(failedAttempts: Int = 0, maxAttempts: Int = 3) {
        try {
            val user = validateUser()
            viewer.show("Welcome ${user.userName}")
            return
        } catch (ex: InvalidCredentialsException) {
            viewer.show("user name or password can't be empty")
            if (failedAttempts < maxAttempts) {
                authenticateLoop(failedAttempts + 1, maxAttempts)
            }
        }
    }
}
