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
            user.onSuccess {  userData->
                val userId=userData?.id
                val userName=userData?.userName
                val permission=userData?.permission
                val role=userData?.role
                viewer.show(listOf(userId,userName,permission,role).toString()) }


        }catch(e:Exception){
            viewer.show("no user logged in,please log in")
        }


    }
}