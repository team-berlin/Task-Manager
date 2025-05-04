package com.berlin.domain.usecase.authService

import com.berlin.domain.repository.AuthenticationRepository
import com.berlin.domain.helper.AuthServiceTestData
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GettingUsersLoggedInUseCaseTest {
    private lateinit var repository: AuthenticationRepository
    private lateinit var gettingUsersLoggedInUseCase: GettingUsersLoggedInUseCase

    @BeforeEach
    fun setup() {
        repository = mockk()
        gettingUsersLoggedInUseCase = GettingUsersLoggedInUseCase(repository)
    }



    @Test
    fun `getCurrentUser returns admin when admin is logged in`() {
        // Given
        val expectedAdminUser = AuthServiceTestData.adminIsFirstUser
        every { repository.getCurrentUser() } returns Result.success(expectedAdminUser)

        // When
        val result = gettingUsersLoggedInUseCase.getCurrentUser()

        // Then
        assertThat(result).isEqualTo(Result.success(expectedAdminUser))
    }

}