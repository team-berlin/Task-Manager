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
        try {
            val user=getUserLoggedIn.getCurrentUser()
            try {
                val usertoShow= listOf(user?.id,user?.userName,user?.permission,user?.role)
                viewer.show(usertoShow.toString())
            }catch(e:Exception){
                viewer.show("no user logged in,please log in")
            }

        }catch(e:Exception){
            viewer.show("no user logged in,please log in")
        }


    }
}