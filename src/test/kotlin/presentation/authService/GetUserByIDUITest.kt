package presentation.authService

import com.berlin.domain.helper.AuthServiceTestData
import com.berlin.domain.model.User
import com.berlin.domain.model.UserRole
import com.berlin.domain.usecase.authService.GetUserByIDUseCase
import com.berlin.presentation.authService.GetUserByIDUI
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import com.google.common.truth.Truth.assertThat
import io.mockk.*
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
        // Given
        val id = AuthServiceTestData.idExist
        every { reader.read() } returns id
        every { useCase.getUserById(id) } returns Result.success(AuthServiceTestData.existingUser)

        // When
        ui.run()

        // Then
        verify { useCase.getUserById(id) }
        assertThat(printed).contains("Enter the user id: ")
    }
    @Test
    fun `run should show user info when valid user ID is entered`() {
        val user = User(id = "123", userName = "Alice", password = "pass", role = UserRole.MATE)
        every { reader.read() } returns "123"
        every { useCase.getUserById("123") } returns Result.success(user)
        every { viewer.show(any()) } just Runs

        ui.run()

        verify(exactly = 1) { viewer.show("Enter the user id: ") }
        verify(exactly = 3) { viewer.show(match { it.contains("ID:") || it.contains("Name:") || it.contains("role:") }) }
    }
    @Test
    fun `run should show error when invalid user ID is entered`() {
        every { reader.read() } returns "invalid-id"
        every { useCase.getUserById("invalid-id") } returns Result.failure(Exception("User not found"))
        every { viewer.show(any()) } just Runs

        ui.run()

        verify(exactly = 2) { viewer.show(any()) }
    }
    @Test
    fun `run should show error when user ID is blank`() {
        // Arrange
        every { reader.read() } returns "   "
        every { useCase.getUserById("") } returns Result.failure(Exception("blank id"))
        every { viewer.show(any()) } just Runs

        // Act
        ui.run()

        // Assert
        verify(exactly = 2) { viewer.show(any()) } // prompt + failure
    }


}