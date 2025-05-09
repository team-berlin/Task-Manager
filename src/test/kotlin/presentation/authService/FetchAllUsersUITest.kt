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

class FetchAllUsersUITest {
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
            "====================="
        )
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
    fun `should return users when there is users`() {
        every { useCase.getAllUsers() } returns Result.success(users)

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

    private companion object {
        val users = listOf(
            User(id = "1", userName = "Menna", password = "12345678j", role = UserRole.ADMIN),
            User(id = "2", userName = "Sarah", password = "1234567890", role = UserRole.MATE)
        )
    }

}