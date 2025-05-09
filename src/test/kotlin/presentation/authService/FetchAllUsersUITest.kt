package presentation.authService

import com.berlin.domain.model.User
import com.berlin.domain.model.UserRole
import com.berlin.domain.usecase.authService.FetchAllUsersUseCase
import com.berlin.presentation.authService.FetchAllUsersUI
import com.berlin.presentation.io.Viewer
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FetchAllUsersUseCaseTest {
    private lateinit var viewer: Viewer
    private lateinit var useCase: FetchAllUsersUseCase
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
        // Given
        val users = listOf(
            User(id = "1", userName = "Menna", password = "12345678j",  role =UserRole.ADMIN),
            User(id = "2", userName = "Sarah", password = "1234567890",  role = UserRole.MATE)
        )
        every { useCase.getAllUsers() } returns Result.success(users)

        // When
        ui.run()

        // Then
        assertThat(printed).containsExactly(
            "ID: 1",
            "Name: Menna",
            "role: ADMIN",
            "=====================",
            "ID: 2",
            "Name: Sarah",
            "role: MATE",
            "=====================")
    }

    @Test
    fun `should print message when no users are found`() {
        // Given
        every { useCase.getAllUsers() } returns Result.success(listOf())

        // When
        ui.run()

        // Then
        assertThat(printed).containsExactly(
            "No users found."
        )
    }
    @Test
    fun `should s display "No users found" when there are no users`() {
        // Given
        val emptyUserList = emptyList<User>()

        val mockViewer = mockk<Viewer>(relaxed = true)
        val useCase = mockk<FetchAllUsersUseCase>()
        every { useCase.getAllUsers() } returns Result.success(emptyUserList)

        val ui = FetchAllUsersUI(fetchAllUsers = useCase, viewer = mockViewer)

        // When
        ui.run()

        // Then
        verify { mockViewer.show("No users found.") }
    }
    @Test
    fun `run should show user info for each user when users are returned`() {
        // Arrange
        val user1 = User("1", "Alice", "pass", UserRole.MATE)
        val user2 = User("2", "Bob", "1234", UserRole.ADMIN)
        val userList = listOf(user1, user2)

        every { useCase.getAllUsers() } returns Result.success(userList)
        every { viewer.show(any()) } just Runs

        // Act
        ui.run()

        // Assert
        verify(exactly = 8) { viewer.show(any()) } // 4 calls per user
    }

    @Test
    fun `run should show message when user list is empty`() {
        // Arrange
        every { useCase.getAllUsers() } returns Result.success(emptyList())
        every { viewer.show(any()) } just Runs

        // Act
        ui.run()

        // Assert
        verify(exactly = 1) { viewer.show(any()) }
    }

    @Test
    fun `run should show nothing when fetchAllUsers returns failure`() {
        // Arrange
        every { useCase.getAllUsers() } returns Result.failure(Exception("error"))
        every { viewer.show(any()) } just Runs

        // Act
        ui.run()

        // Assert
        verify(exactly = 0) { viewer.show(any()) }
    }





}



