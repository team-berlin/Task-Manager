package com.berlin.presentation.authService

import com.berlin.domain.exception.InvalidUserIdException
import com.berlin.domain.exception.TaskNotFoundException
import com.berlin.domain.exception.UserNotFoundException
import com.berlin.domain.model.User
import com.berlin.domain.repository.AuthenticationRepository
import com.berlin.domain.usecase.authService.GetUserByIDUseCase
import com.berlin.presentation.UiRunner
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import domain.usecase.authService.AuthenticateUserUseCase

class GetUserByIDUI(
    private val useCase: GetUserByIDUseCase,
    private val viewer: Viewer,
    private val reader: Reader,
) : UiRunner {
    override val id: Int = 1
    override val label: String = "Get User by ID"
    override fun run() {
        try {
            viewer.show("Enter ID to Search for user: ")
            val id = reader.read()?.trim().orEmpty()
            useCase.getUserById(id)
                .onSuccess { showUser(it) }
                .onFailure { ex ->
                    when (ex) {
                        is UserNotFoundException ->
                            viewer.show("No User found with ID “$id”")

                        else ->
                            viewer.show(ex.message ?: "Lookup failed")
                    }
                }

        } catch (e: InvalidUserIdException) {
            viewer.show("Invalid user id")
        }
    }

    private fun showUser(user: User) {
        viewer.show("ID: ${user.id}")
        viewer.show("UserName: ${user.userName}")
        viewer.show("Role: ${user.role}")
    }
}