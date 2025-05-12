package com.berlin.presentation.authService

import com.berlin.domain.model.Permission
import com.berlin.domain.model.user.User
import com.berlin.domain.usecase.authService.GetUserByIDUseCase
import com.berlin.presentation.PermissionedUiRunner
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer

class GetUserByIDUI(
    private val getUserByIDUseCase: GetUserByIDUseCase,
    private val viewer: Viewer,
    private val reader: Reader,
) : PermissionedUiRunner {

    override val id: Int = 4
    override val label: String = "get user by id"

    override fun isAllowed(permission: Permission) = permission.getUserById

    override fun run() {
        viewer.show("Enter the user id: ")
        val id = reader.read()?.trim().orEmpty()
       val user =  getUserByIDUseCase.getUserById(id)
          showUserInfo(user)
    }
    private fun showUserInfo(user: User) {
        viewer.show("ID: ${user.id}")
        viewer.show(" Name: ${user.userName}")
        viewer.show(" role: ${user.role}")
    }
}