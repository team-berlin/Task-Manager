package com.berlin.domain.usecase.authService

import com.berlin.domain.exception.InvalidUserIdException
import com.berlin.domain.repository.AuthenticationRepository
import com.berlin.domain.helper.AuthServiceTestData
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class GetUserByIDUseCaseTest {
    private lateinit var repository: AuthenticationRepository
    private lateinit var getUserByIDUseCase: GetUserByIDUseCase

    @BeforeEach
    fun setup() {
        repository = mockk()
        getUserByIDUseCase = GetUserByIDUseCase(repository)
    }


    @Test
    fun `getUserById throws InvalidUserIdException when ID is empty`() = runTest {
        // When & Then
        assertThrows<InvalidUserIdException> {
            getUserByIDUseCase.getUserById("")
        }
    }

    @Test
    fun `getUserById returns user when ID exists`() = runTest {
        // Given
        val existingId = AuthServiceTestData.idExist
        val expectedUser = AuthServiceTestData.existingUser
        coEvery { repository.getUserById(existingId) } returns Result.success(expectedUser)

        // When
        val result = getUserByIDUseCase.getUserById(existingId)

        // Then
        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `throws Invalid User Id Exception when id is blank`() = runTest {
        assertThrows<InvalidUserIdException> {
            getUserByIDUseCase.getUserById("   ")
        }
        coVerify(exactly = 0) { repository.getUserById(any()) }
    }
}