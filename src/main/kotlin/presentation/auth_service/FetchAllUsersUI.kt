package com.berlin.presentation.authService

import com.berlin.domain.model.Permission
import com.berlin.domain.model.user.User
import com.berlin.domain.usecase.authService.GetAllUsersUseCase
import com.berlin.presentation.PermissionedUiRunner
import com.berlin.presentation.io.Viewer

class FetchAllUsersUI(
    private val fetchAllUsers: GetAllUsersUseCase,
    private val viewer: Viewer,
) : PermissionedUiRunner {

    override val id: Int = 2
    override val label: String = "fetch all users"

    override fun isAllowed(permission: Permission) = permission.fetchAllUsers

    override fun run() {
        val users = fetchAllUsers()
        users.forEach { user ->
            showUserInfo(user)
        }
    }

    private fun showUserInfo(user: User) {
        viewer.show("ID: ${user.id}")
        viewer.show("Name: ${user.userName}")
        viewer.show("role: ${user.role}")
        viewer.show("=====================")
    }
}

