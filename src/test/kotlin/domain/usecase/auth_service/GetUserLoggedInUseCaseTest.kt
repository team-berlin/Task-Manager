package com.berlin.domain.usecase.authService

import com.berlin.domain.helper.AuthServiceTestData.adminIsFirstUser
import com.berlin.domain.repository.AuthenticationRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetUserLoggedInUseCaseTest {
    private lateinit var repository: AuthenticationRepository
    private lateinit var gettingUsersLoggedInUseCase: GetUserLoggedInUseCase

    @BeforeEach
    fun setup() {
        repository = mockk()
        gettingUsersLoggedInUseCase = GetUserLoggedInUseCase(repository)
    }

    @Test
    fun `getCurrentUser returns admin when admin is logged in`() {
        every { repository.getCurrentUser() } returns adminIsFirstUser

        val result = gettingUsersLoggedInUseCase()

        assertThat(result).isEqualTo(adminIsFirstUser)
    }

}