package com.berlin.presentation.authService

import com.berlin.domain.model.Permission
import com.berlin.domain.model.user.User
import com.berlin.domain.usecase.authService.GetAllUsersUseCase
import com.berlin.presentation.io.Viewer
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FetchAllUsersUITest {
    private lateinit var viewer: Viewer
    private lateinit var useCase: GetAllUsersUseCase
    private lateinit var ui: FetchAllUsersUI
    private val printed = mutableListOf<String>()

    @BeforeEach
    fun setUp() {
        viewer = mockk(relaxed = true) {
            every { show(capture(printed)) } just Runs
        }
        useCase = mockk()
        ui = FetchAllUsersUI(useCase, viewer)
        printed.clear()
    }

    @Test
    fun `should print all users when users are available`() {
        every { useCase() } returns users

        ui.run()

        assertThat(printed).containsExactly(
            "ID: 1",
            "Name: Menna",
            "role: ADMIN",
            "=====================",
            "ID: 2",
            "Name: Sarah",
            "role: MATE",
            "====================="
        )
    }

    @Test
    fun `should return users when there is users`() {
        every { useCase() } returns users

        ui.run()

        verifySequence {
            viewer.show("ID: ${users[0].id}")
            viewer.show("Name: ${users[0].userName}")
            viewer.show("role: ${users[0].role}")
            viewer.show("=====================")
            viewer.show("ID: ${users[1].id}")
            viewer.show("Name: ${users[1].userName}")
            viewer.show("role: ${users[1].role}")
            viewer.show("=====================")
        }
    }
    @Test
    fun `isAllowed should return true when permission is granted for fetching all users`() {
        val permission = mockk<Permission>()
        every { permission.fetchAllUsers } returns true

        val result = ui.isAllowed(permission)

        assertThat(result).isTrue()
    }

    @Test
    fun `isAllowed should return false when permission is denied for fetching all users`() {
        val permission = mockk<Permission>()
        every { permission.fetchAllUsers } returns false

        val result = ui.isAllowed(permission)
        assertThat(result).isFalse() 
    }


    private companion object {
        val users = listOf(
            User(id = "1", userName = "Menna", role = User.UserRole.ADMIN),
            User(id = "2", userName = "Sarah", role = User.UserRole.MATE)
        )
    }

}