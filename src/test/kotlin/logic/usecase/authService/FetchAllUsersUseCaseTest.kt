package com.berlin.logic.usecase.authService

import com.berlin.AuthServiceTestData
import com.berlin.logic.repositories.AuthenticationRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FetchAllUsersUseCaseTest {
    private lateinit var repository: AuthenticationRepository
    private lateinit var fetchAllUsersUseCase : FetchAllUsersUseCase

    @BeforeEach
    fun setup() {
        repository = mockk()
        fetchAllUsersUseCase= FetchAllUsersUseCase(repository)
    }

    @Test
    fun `getAllUsers should return one user at least who is admin when there is no mate created yet`() {
        //Given
        every { repository.getAllUsers() } returns listOf(AuthServiceTestData.adminIsFirstUser)
        //when
        val result = fetchAllUsersUseCase.getAllUsers()
        // then
        assertThat(result).isEqualTo(listOf(AuthServiceTestData.adminIsFirstUser))

    }
}