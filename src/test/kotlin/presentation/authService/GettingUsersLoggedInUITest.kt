package com.berlin.presentation.authService

import com.berlin.domain.exception.UserNotLoggedInException
import com.berlin.domain.model.User
import com.berlin.domain.model.UserRole
import com.berlin.domain.usecase.authService.GetUserLoggedInUseCase
import com.berlin.presentation.authService.GettingUsersLoggedInUI
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GettingUsersLoggedInUITest {
    private lateinit var getUserLoggedIn: GetUserLoggedInUseCase
    private lateinit var gettingUsersLoggedInUI: GettingUsersLoggedInUI
    private lateinit var viewer: Viewer
    private lateinit var reader: Reader


    @BeforeEach
    fun setup() {
        getUserLoggedIn = mockk()
        viewer = mockk(relaxed = true)
        reader = mockk()
        gettingUsersLoggedInUI = GettingUsersLoggedInUI(getUserLoggedIn, viewer)
    }

    @Test
    fun `gettingUserLoggedIn should return current user when some one logged in`() {
        every { getUserLoggedIn.getCurrentUser() } returns Result.success(cachedUser)

        gettingUsersLoggedInUI.run()

        verifySequence {
            viewer.show("ID: ${cachedUser.id}")

            viewer.show(" Name: ${cachedUser.userName}")
            viewer.show(" role: ${cachedUser.role}")
        }
    }

    @Test
    fun `gettinguserLoggedIn should return error message when no one logged in`() {
        every { getUserLoggedIn.getCurrentUser() } returns Result.failure(UserNotLoggedInException("No one logged in"))
        gettingUsersLoggedInUI.run()
        verify { viewer.show("no user logged in,please log in") }
    }

    private companion object {
        val cachedUser = User(id = "1", userName = "Menna", password = "12345678j", role = UserRole.ADMIN)
    }
}