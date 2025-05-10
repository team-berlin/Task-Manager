package presentation.authService

import com.berlin.domain.model.User
import com.berlin.domain.model.UserRole
import com.berlin.domain.usecase.authService.FetchAllUsersUseCase
import com.berlin.presentation.authService.FetchAllUsersUI
import com.berlin.presentation.io.Viewer
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import kotlinx.coroutines.test.runTest
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
            coEvery { show(capture(printed)) } just Runs
        }
        useCase = mockk()
        ui = FetchAllUsersUI(useCase, viewer)
        printed.clear()
    }

    @Test
    fun `should print all users when users are available`() = runTest {
        // Given
        val users = listOf(
            User(id = "1", userName = "Menna", password = "12345678j",  role =UserRole.ADMIN),
            User(id = "2", userName = "Sarah", password = "1234567890",  role = UserRole.MATE)
        )
        coEvery { useCase.getAllUsers() } returns Result.success(users)

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
    fun `should print message when no users are found`() = runTest {
        // Given
        coEvery { useCase.getAllUsers() } returns Result.success(listOf())

        // When
        ui.run()

        // Then
        assertThat(printed).containsExactly(
            "No users found."
        )
    }
}