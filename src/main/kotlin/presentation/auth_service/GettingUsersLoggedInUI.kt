package com.berlin.presentation.authService

import com.berlin.domain.model.Permission
import com.berlin.domain.model.user.User
import com.berlin.domain.usecase.authService.GetUserLoggedInUseCase
import com.berlin.presentation.PermissionedUiRunner
import com.berlin.presentation.io.Viewer

class GettingUsersLoggedInUI(
    private val getUserLoggedIn: GetUserLoggedInUseCase,
    private val viewer: Viewer,
) : PermissionedUiRunner {

    override val id: Int = 3
    override val label: String = "get user logged in"

    override fun isAllowed(permission: Permission) = permission.getLoggedInUsers

    override fun run() {
        val user = getUserLoggedIn()
          showUserInfo(user)
    }

    private fun showUserInfo(user: User) {
        viewer.show("ID: ${user.id}")
        viewer.show(" Name: ${user.userName}")
        viewer.show(" role: ${user.role}")
    }
}