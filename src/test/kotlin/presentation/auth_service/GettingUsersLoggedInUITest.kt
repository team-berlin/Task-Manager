package com.berlin.presentation.authService

import com.berlin.domain.model.Permission
import com.berlin.domain.model.user.User
import com.berlin.domain.usecase.authService.GetUserLoggedInUseCase
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifySequence
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
        every { getUserLoggedIn() } returns cachedUser

        gettingUsersLoggedInUI.run()

        verifySequence {
            viewer.show("ID: ${cachedUser.id}")

            viewer.show(" Name: ${cachedUser.userName}")
            viewer.show(" role: ${cachedUser.role}")
        }
    }

    @Test
    fun `getting user logged in should display user info when a user is logged in`() {
        val loggedInUser = User(id = "1", userName = "Menna", role = User.UserRole.ADMIN)
        every { getUserLoggedIn() } returns loggedInUser

        gettingUsersLoggedInUI.run()

        verify {
            viewer.show("ID: ${loggedInUser.id}")
            viewer.show(" Name: ${loggedInUser.userName}")
            viewer.show(" role: ${loggedInUser.role}")
        }
    }

    @Test
    fun `isAllowed should return true when permission is granted for fetching all users`() {
        val permission = mockk<Permission>()
        every { permission.getLoggedInUsers } returns true

        val result = gettingUsersLoggedInUI.isAllowed(permission)

        assertThat(result).isTrue()
    }
    @Test
    fun `isAllowed should return false when permission is denied for fetching all users`() {
        val permission = mockk<Permission>()
        every { permission.getLoggedInUsers } returns false

        val result = gettingUsersLoggedInUI.isAllowed(permission)

        assertThat(result).isFalse()
    }

    private companion object {
        val cachedUser = User(id = "1", userName = "Menna", role = User.UserRole.ADMIN)
    }
}