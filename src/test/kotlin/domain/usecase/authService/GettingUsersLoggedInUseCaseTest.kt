package com.berlin.domain.usecase.authService

import com.berlin.domain.repository.AuthenticationRepository
import com.berlin.domain.helper.AuthServiceTestData
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
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
    fun `getCurrentUser returns admin when admin is logged in`() = runTest {
        // Given
        val expectedAdminUser = AuthServiceTestData.adminIsFirstUser
        coEvery { repository.getCurrentUser() } returns Result.success(expectedAdminUser)

        // When
        val result = gettingUsersLoggedInUseCase.getCurrentUser()

        // Then
        assertThat(result).isEqualTo(Result.success(expectedAdminUser))
    }

}