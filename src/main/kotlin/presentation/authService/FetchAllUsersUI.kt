package com.berlin.presentation.authService

import com.berlin.domain.model.User
import com.berlin.domain.usecase.authService.FetchAllUsersUseCase
import com.berlin.presentation.UiRunner
import com.berlin.presentation.io.Viewer

class FetchAllUsersUI(
    private val fetchAllUsers: FetchAllUsersUseCase,
    private val viewer: Viewer,
) : UiRunner {
    override val id: Int = 500
    override val label: String = "fetch all users"
    override suspend fun run() {
        val users = fetchAllUsers.getAllUsers()
        users.onSuccess { usersList ->
            if (usersList.isEmpty())
                viewer.show("No users found.")
            else usersList.forEach { user -> showUserInfo(user) }
        }
    }

    private fun showUserInfo(user: User) {
        viewer.show("ID: ${user.id}")
        viewer.show("Name: ${user.userName}")
        viewer.show("role: ${user.role}")
        viewer.show("=====================")
    }
}

