package com.berlin.presentation.authService

import com.berlin.domain.usecase.authService.GettingUsersLoggedInUseCase
import com.berlin.presentation.UiRunner
import com.berlin.presentation.io.Viewer

class GettingUsersLoggedInUI(
    private val getUserLoggedIn: GettingUsersLoggedInUseCase,
    private val viewer: Viewer,
) : UiRunner {
    override val id: Int = 100
    override val label: String = "get user logged in"
    override fun run() {
        val user = getUserLoggedIn.getCurrentUser()
        user.fold(onSuccess = {
            viewer.show("Welcome ${it?.userName}")
        }, onFailure = {
            viewer.show("no user logged in,please log in")
        })
    }
}