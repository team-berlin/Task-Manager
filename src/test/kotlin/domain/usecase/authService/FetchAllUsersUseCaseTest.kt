package com.berlin.domain.usecase.authService

import com.berlin.domain.repository.AuthenticationRepository
import com.berlin.domain.helper.AuthServiceTestData
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FetchAllUsersUseCaseTest {

    private lateinit var repository: AuthenticationRepository
    private lateinit var fetchAllUsersUseCase: FetchAllUsersUseCase

    @BeforeEach
    fun setup() {
        repository = mockk()
        fetchAllUsersUseCase = FetchAllUsersUseCase(repository)
    }

    @Test
    fun `getAllUsers returns admin as first user when no mates exist`() {
        // Given
        val expectedAdminUser = AuthServiceTestData.adminIsFirstUser
        every { repository.getAllUsers() } returns Result.success(listOf(expectedAdminUser))

        // When
        val result = fetchAllUsersUseCase.getAllUsers()

        // Then
        assertThat(result).isEqualTo(Result.success(listOf(expectedAdminUser)))
    }
}
