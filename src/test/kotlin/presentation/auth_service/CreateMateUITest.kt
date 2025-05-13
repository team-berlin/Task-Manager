package com.berlin.presentation.authService

import com.berlin.domain.exception.InvalidCredentialsException
import com.berlin.domain.helper.AuthServiceTestData
import com.berlin.domain.helper.AuthServiceTestData.user
import com.berlin.domain.model.Permission
import com.berlin.domain.model.user.User
import com.berlin.domain.usecase.authService.CreateMateUseCase
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CreateMateUITest {
    private lateinit var createMateUseCase: CreateMateUseCase
    private lateinit var creationOfMateUi: CreateMateUI
    private lateinit var viewer: Viewer
    private lateinit var reader: Reader


    @BeforeEach
    fun setup() {
        createMateUseCase = mockk()
        viewer = mockk(relaxed = true)
        reader = mockk()
        creationOfMateUi = CreateMateUI(createMateUseCase, viewer, reader)
    }

    @Test
    fun `run should show success message when user creation succeeds`() {
        every { reader.read() } returnsMany listOf(AuthServiceTestData.testForUserName, AuthServiceTestData.testForUserPassword)
        every { createMateUseCase(AuthServiceTestData.testForUserName, AuthServiceTestData.testForUserPassword) } returns user

        creationOfMateUi.run()

        verify { viewer.show(any()) }
    }
    @Test
    fun `isAllowed should return true when permission is granted for fetching all users`() {
        val permission = mockk<Permission>()
        every { permission.createMate } returns true

        val result = creationOfMateUi.isAllowed(permission)

        assertThat(result).isTrue()
    }

    @Test
    fun `isAllowed should return false when permission is denied for fetching all users`() {
        val permission = mockk<Permission>()
        every { permission.createMate } returns false

        val result = creationOfMateUi.isAllowed(permission)
        assertThat(result).isFalse()
    }


    @Test
    fun `handleMateCreation should not show success message if username is empty`() {
        val reader = mockk<Reader>()
        val viewer = mockk<Viewer>(relaxed = true)
        val createMateUseCase = mockk<CreateMateUseCase>()

        every { reader.read() } returnsMany listOf("   ", "pass")
        every { createMateUseCase("", "pass") } returns User("","",User.UserRole.MATE)

        val ui = CreateMateUI(createMateUseCase, viewer, reader)

        ui.run()

        verify { createMateUseCase("", "pass") }
        verify(exactly = 0) { viewer.show("$createMateUseCase is successfully created!") }
    }

    @Test
    fun `should show custom message when InvalidCredentialsException has message`() {
        val viewer = mockk<Viewer>(relaxed = true)
        val reader = mockk<Reader>()
        val createMateUseCase = mockk<CreateMateUseCase>()

        every { reader.read() } returnsMany listOf("A", "B")
        every { createMateUseCase(any(), any()) } throws InvalidCredentialsException("Invalid input")

        val ui = CreateMateUI(createMateUseCase, viewer, reader)
        ui.run()

        verify { viewer.show("Invalid input") }
    }

    @Test
    fun `should show exception message when InvalidCredentialsException has message`() {
        val viewer = mockk<Viewer>(relaxed = true)
        val reader = mockk<Reader>()
        val useCase = mockk<CreateMateUseCase>()

        every { reader.read() } returnsMany listOf("A", "B")
        every { useCase(any(), any()) } throws InvalidCredentialsException("Custom error")

        val ui = CreateMateUI(useCase, viewer, reader)
        ui.run()

        verify { viewer.show("Custom error") }
    }
    @Test
    fun `should show default message when InvalidCredentialsException message is null`() {
        val viewer = mockk<Viewer>(relaxed = true)
        val reader = mockk<Reader>()
        val useCase = mockk<CreateMateUseCase>()

        every { reader.read() } returnsMany listOf("X", "Y")
        every { useCase(any(), any()) } throws InvalidCredentialsException("some thing went wrong please try again!")

        val ui = CreateMateUI(useCase, viewer, reader)
        ui.run()

        verify { viewer.show("some thing went wrong please try again!") }
    }
    @Test
    fun `should show exception message when InvalidCredentialsException has a custom message`() {

        every { reader.read() } returnsMany listOf("john", "doe123")
        every { createMateUseCase(any(), any()) } throws InvalidCredentialsException("Username already exists!")

        creationOfMateUi.run()

        verify { viewer.show("Username already exists!") }
    }

    @Test
    fun `should trim whitespace from password input`() {
        val viewer = mockk<Viewer>(relaxed = true)
        val reader = mockk<Reader>()
        val useCase = mockk<CreateMateUseCase>()

        every { reader.read() } returnsMany listOf("John", "   secret123   ")
        val user = User("u512","John", User.UserRole.MATE)
        every { useCase("John", "secret123") } returns user

        val ui = CreateMateUI(useCase, viewer, reader)
        ui.run()

        verify { useCase("John", "secret123") }
        verify { viewer.show("$useCase is successfully created!") }
    }
}
