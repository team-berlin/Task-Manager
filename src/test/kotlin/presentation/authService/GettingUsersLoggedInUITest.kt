package presentation.authService


import com.berlin.domain.model.User
import com.berlin.domain.model.UserRole
import com.berlin.domain.usecase.authService.GetUserLoggedInUseCase
import com.berlin.presentation.authService.GettingUsersLoggedInUI
import com.berlin.presentation.io.Viewer
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test


class GettingUsersLoggedInUITest {

    private lateinit var getUserLoggedIn: GetUserLoggedInUseCase
    private lateinit var viewer: Viewer
    private lateinit var ui: GettingUsersLoggedInUI

    @BeforeEach
    fun setup() {
        getUserLoggedIn = mockk()
        viewer = mockk(relaxed = true)
        ui = GettingUsersLoggedInUI(getUserLoggedIn, viewer)
    }

    @org.junit.jupiter.api.Test
    fun `run should show 3 viewer messages when user is logged in`() {
        // Given
        val user = User(id = "1", userName = "Alice", password = "742", role = UserRole.ADMIN)
        every { getUserLoggedIn.getCurrentUser() } returns Result.success(user)

        // When
        ui.run()

        // Then
        verify(exactly = 3) { viewer.show(any()) }
    }

    @org.junit.jupiter.api.Test
    fun `run should show one viewer message when no user is logged in`() {
        // Given
        every { getUserLoggedIn.getCurrentUser() } returns Result.failure(Exception("no user"))

        // When
        ui.run()

        // Then
        verify(exactly = 1) { viewer.show(any()) }
    }

    @Test
    fun `run should show 3 messages when user is logged in`() {
        // Arrange
        val user = User(id = "123", userName = "Alice", password = "pass", role = UserRole.ADMIN)
        every { getUserLoggedIn.getCurrentUser() } returns Result.success(user)
        every { viewer.show(any()) } just Runs

        // Act
        ui.run()

        // Assert
        verify(exactly = 3) { viewer.show(any()) }
    }
    @Test
    fun `run should show login prompt when user is not logged in`() {
        // Arrange
        every { getUserLoggedIn.getCurrentUser() } returns Result.failure(Exception("no user"))
        every { viewer.show(any()) } just Runs

        // Act
        ui.run()

        // Assert
        verify(exactly = 1) { viewer.show(any()) }
    }


}




