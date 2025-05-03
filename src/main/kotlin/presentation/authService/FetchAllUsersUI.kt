package com.berlin.presentation.authService

import com.berlin.domain.usecase.authService.FetchAllUsersUseCase
import com.berlin.presentation.UiRunner
import com.berlin.presentation.io.Viewer

class FetchAllUsersUI(
    private val fetchAllUsers: FetchAllUsersUseCase,
    private val viewer: Viewer,
) : UiRunner {
    override val id: Int = 500
    override val label: String = "fetch all users"
    override fun run() {
        val users = fetchAllUsers.getAllUsers()
        users.onSuccess {
            if (it.isEmpty()) {
                viewer.show("No users found.")
            } else {
                users.onSuccess { usersList ->
                    usersList.forEach { user ->
                        viewer.show("ID: ${user.id}, Name: ${user.userName}")
                    }
                }
            }
        }
    }
}
