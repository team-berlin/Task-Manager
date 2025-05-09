package com.berlin.presentation.authService

import com.berlin.domain.model.User
import com.berlin.domain.usecase.authService.CreateMateUseCase
import com.berlin.presentation.UiRunner
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer

class CreateMateUI(
    private val createMateUseCase: CreateMateUseCase,
    private val viewer: Viewer,
    private val reader: Reader,
): UiRunner {
    override val id: Int = 300
    override val label: String = "Create new mate"
    override fun run() {
        handleMateCreation()
    }
    private fun createMate(): Result<User>{
        viewer.show("Enter user name or x to exit: ")
        val userName = reader.read()?.trim().orEmpty()
        viewer.show("Enter user password: ")
        val userPassword = reader.read()?.trim().orEmpty()
        return createMateUseCase.createMate(userName, userPassword)
    }
    private fun handleMateCreation(attempt: Int = 0, maxAttempts: Int = 3) {
        createMate().onSuccess {
            viewer.show("New mate is successfully created!")
        }.onFailure {
            viewer.show(it.message ?: "some thing went wrong please try again!")
            if (attempt < maxAttempts) {
                handleMateCreation(attempt + 1, maxAttempts)
            }
        }
    }

}