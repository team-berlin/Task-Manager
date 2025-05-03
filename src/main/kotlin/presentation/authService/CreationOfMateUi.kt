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
    override val id: Int = 3
    override val label: String = "Create new mate"
    override fun run() {
        handleMateCreation()
    }
    private fun createMate(): Result<User>{
        viewer.show("Enter user name: ")
        val userName = reader.read()?.trim().orEmpty()
        viewer.show("Enter user password: ")
        val userPassword = reader.read()?.trim().orEmpty()
        return creationOfMateUseCase.createMate(userName, userPassword)
    }
    private tailrec fun handleMateCreation(){
        createMate().onSuccess {
            viewer.show("New mate is successfully created!")
        }.onFailure {
            viewer.show("something wrong please try again!")
            handleMateCreation()
        }
    }
}