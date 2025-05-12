package com.berlin.presentation.authService

import com.berlin.domain.exception.InvalidCredentialsException
import com.berlin.domain.model.Permission
import com.berlin.domain.model.user.User
import com.berlin.domain.usecase.authService.CreateMateUseCase
import com.berlin.presentation.PermissionedUiRunner
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer

class CreateMateUI(
    private val createMateUseCase: CreateMateUseCase,
    private val viewer: Viewer,
    private val reader: Reader,
) : PermissionedUiRunner {

    override val id: Int = 1
    override val label: String = "Create new mate"

    override fun isAllowed(permission: Permission) = permission.createMate

    override fun run() {
        handleMateCreation()
    }
    private fun createMate(): User {
        viewer.show("Enter user name or x to exit: ")
        val userName = reader.read()?.trim().orEmpty()
        viewer.show("Enter user password: ")
        val userPassword = reader.read()?.trim().orEmpty()
        val user = createMateUseCase.createMate(userName, userPassword)
        return user
    }
    private fun handleMateCreation(attempt: Int = 0, maxAttempts: Int = 3) {
        try {
            if (createMate().userName.isNotEmpty()){
                viewer.show("$createMateUseCase is successfully created!")
            }
        }catch (ex: InvalidCredentialsException){
            viewer.show(ex.message ?: "some thing went wrong please try again!")
            if (attempt < maxAttempts) {
                handleMateCreation(attempt + 1, maxAttempts)
            }
        }
    }
}