package com.berlin.presentation.authService

import com.berlin.domain.repository.AuthenticationRepository
import com.berlin.domain.usecase.authService.GetUserByIDUseCase
import com.berlin.domain.usecase.authService.GettingUsersLoggedInUseCase
import com.berlin.presentation.UiRunner
import com.berlin.presentation.io.Viewer

class GettingUsersLoggedInUI(
    private val getUserLoggedIn: GettingUsersLoggedInUseCase,
    private val viewer: Viewer,
) : UiRunner {
    override val id: Int = 1
    override val label: String = "Log in"
    override fun run() {
        try {
            viewer.show("Enter the user id: ")
            getUserLoggedIn.getCurrentUser()
        }catch(e:Exception){
            viewer.show("no user logged in,please log in")
        }


    }
}