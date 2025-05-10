package com.berlin.presentation.authService

import com.berlin.domain.model.User
import com.berlin.domain.usecase.authService.CreationOfMateUseCase
import com.berlin.presentation.UiRunner
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer

class CreationOfMateUi(
    private val creationOfMateUseCase: CreationOfMateUseCase,
    private val viewer: Viewer,
    private val reader: Reader,
): UiRunner {
    override val id: Int = 300
    override val label: String = "Create new mate"
    override suspend fun run() {
        handleMateCreation()
    }
    private suspend fun createMate(): Result<User>{
        viewer.show("Enter user name: ")
        val userName = reader.read()?.trim().orEmpty()
        viewer.show("Enter user password: ")
        val userPassword = reader.read()?.trim().orEmpty()
        return creationOfMateUseCase.createMate(userName, userPassword)
    }
    private suspend fun handleMateCreation(attempt: Int = 0, maxAttempts: Int = 3) {
        createMate().onSuccess {
            viewer.show("New mate is successfully created!")
        }.onFailure {
            viewer.show("something wrong please try again!")
            if (attempt < maxAttempts) {
                handleMateCreation(attempt + 1, maxAttempts)
            }
        }
    }


}