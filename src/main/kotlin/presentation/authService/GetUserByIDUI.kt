package com.berlin.presentation.authService

import com.berlin.domain.exception.InvalidUserIdException
import com.berlin.domain.exception.UserNotFoundException
import com.berlin.domain.model.User
import com.berlin.domain.usecase.authService.GetUserByIDUseCase
import com.berlin.presentation.UiRunner
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer

class GetUserByIDUI(
    private val getUserByIDUseCase: GetUserByIDUseCase,
    private val viewer: Viewer,
    private val reader: Reader,
) : UiRunner {
    override val id: Int = 900
    override val label: String = "get user by id"

    override fun run() {
        viewer.show("Enter the user id: ")
        val id = reader.read()?.trim().orEmpty()
        try {
            //val id = reader.read()?.trim().orEmpty()
            val user = getUserByIDUseCase.getUserById(id)
            user.fold(
                onSuccess = { showUserInfo(it) },

                onFailure = { ex ->
                    when (ex) {
                        is UserNotFoundException ->
                            viewer.show("No user found for this ID")

                        else -> viewer.show("error: $ex")
                    }
                }
            )
        } catch (e: InvalidUserIdException) {
            viewer.show("Invalid ID")
        }
    }

    private fun showUserInfo(user: User) {
        viewer.show("ID: ${user.id}")
        viewer.show(" Name: ${user.userName}")
        viewer.show(" role: ${user.role}")
    }
}