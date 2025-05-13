package com.berlin.presentation.auth_service

import com.berlin.domain.exception.InvalidUserIdException
import com.berlin.domain.exception.UserNotFoundException
import com.berlin.domain.helper.AuthServiceTestData
import com.berlin.domain.model.Permission
import com.berlin.domain.model.user.User
import com.berlin.domain.usecase.authService.GetUserByIDUseCase
import com.berlin.presentation.authService.GetUserByIDUI
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import com.google.common.truth.Truth.assertThat
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifySequence
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class GetUserByIDUITest {
    private lateinit var viewer: Viewer
    private lateinit var reader: Reader
    private lateinit var useCase: GetUserByIDUseCase
    private lateinit var ui: GetUserByIDUI
    private val printed = mutableListOf<String>()

    @BeforeEach
    fun setUp() {
        viewer = mockk(relaxed = true) {
            every { show(capture(printed)) } just Runs
        }
        reader = mockk()
        useCase = mockk()
        ui = GetUserByIDUI(useCase, viewer, reader)
        printed.clear()
    }

    @Test
    fun `should call use case when correct user ID`() {
        val id = AuthServiceTestData.idExist
        every { reader.read() } returns id
        every { useCase(id) } returns AuthServiceTestData.existingUser

        ui.run()

        verify { useCase(id) }
        assertThat(printed).contains("Enter the user id: ")
    }

    @Test
    fun `should call use case when correct user ID with regardless to spaces in begin of id`() {
        val id = AuthServiceTestData.idWithSpacesExist
        every { reader.read() } returns id.trim()
        every { useCase(id.trim()) } returns AuthServiceTestData.existingUser

        ui.run()

        verify { useCase(id.trim()) }
        assertThat(printed).contains("Enter the user id: ")
    }

    @Test
    fun `should print invalid user id when id not exists`() {
        val id = AuthServiceTestData.idNotExist
        every { reader.read() } returns id
        every { useCase(id) } throws InvalidUserIdException("User ID can't be empty or just digits")

        ui.run()

        assertThat(printed).contains("invalid user id")
    }


    @Test
    fun `should return error when id is empty or just spaces`() {
        every { reader.read() } returns " "
        every { useCase("") } throws InvalidUserIdException("User ID can't be empty or just digits")

        ui.run()

        assertThat(printed.last()).isEqualTo("invalid user id")
    }

    @Test
    fun `should return error when unexpected exception occurs`() {
        every { reader.read() } returns "id"
        every { useCase("id") } throws InvalidUserIdException("Unexpected error")

        ui.run()

        assertThat(printed.last()).isEqualTo("invalid user id")
    }
    @Test
    fun `run should show error when user not found`() {
        val id = "notExist"
        every { reader.read() } returns id
        every { useCase(id) } throws UserNotFoundException("User not found")

        ui.run()

        verifySequence {
            viewer.show("Enter the user id: ")
            viewer.show("User not found")
        }
    }
    @Test
    fun `run should handle null input and show invalid user id`() {
        every { reader.read() } returns null
        every { useCase("") } throws InvalidUserIdException("ID is required")

        ui.run()

        verifySequence {
            viewer.show("Enter the user id: ")
            viewer.show("invalid user id")
        }
    }
    @Test
    fun `run should show error when ID is invalid`() {
        val id = " "
        every { reader.read() } returns id
        every { useCase(id.trim()) } throws InvalidUserIdException("Invalid ID")

        ui.run()

        verifySequence {
            viewer.show("Enter the user id: ")
            viewer.show("invalid user id")
        }
    }


    @Test
    fun `isAllowed should return true when permission is granted for fetching all users`() {
        val permission = mockk<Permission>()
        every { permission.getUserById } returns true

        val result = ui.isAllowed(permission)

        assertThat(result).isTrue()
    }
    @Test
    fun `isAllowed should return false when permission is denied for fetching all users`() {
        val permission = mockk<Permission>()
        every { permission.getUserById } returns false

        val result = ui.isAllowed(permission)

        assertThat(result).isFalse()
    }

    @Test
    fun `run should show user info when ID is valid`() {
        val id = "validId"
        val user = User(id, "Ahmed", User.UserRole.MATE)

        every { reader.read() } returns id
        every { useCase(id) } returns user

        ui.run()

        verifySequence {
            viewer.show("Enter the user id: ")
            useCase(id)
            viewer.show("ID: ${user.id}")
            viewer.show(" Name: ${user.userName}")
            viewer.show(" role: ${user.role}")
        }
    }


    @Test
    fun `should return user info when valid user ID provided`() {
        val validId = "validID"
        val expectedUser = AuthServiceTestData.existingUser
        every { reader.read() } returns validId
        every { useCase(validId) } returns expectedUser

        ui.run()

        assertThat(printed).containsExactly(
            "Enter the user id: ",
            "ID: ${expectedUser.id}",
            " Name: ${expectedUser.userName}",
            " role: ${expectedUser.role}"
        )
    }
}
