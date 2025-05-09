package com.berlin.domain.usecase.authService

import com.berlin.domain.exception.InvalidUserIdException
import com.berlin.domain.helper.AuthServiceTestData
import com.berlin.domain.repository.AuthenticationRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetUserByIDUseCaseTest {
    private lateinit var repository: AuthenticationRepository
    private lateinit var getUserByIDUseCase: GetUserByIDUseCase

    @BeforeEach
    fun setup() {
        repository = mockk()
        getUserByIDUseCase = GetUserByIDUseCase(repository)
    }


    @Test
    fun `getUserById return InvalidUserIdException when ID is empty`() {
        val existingId = ""
        every { repository.getUserById(existingId)
        }  returns Result.failure(InvalidUserIdException("User ID can't be empty or just digits"))
        val result =  getUserByIDUseCase.getUserById(existingId)
        // When & Then
      assertThat(result.isFailure)

    }

    @Test
    fun `getUserById returns user when ID exists`() {
        // Given
        val existingId = AuthServiceTestData.idExist
        val expectedUser = AuthServiceTestData.existingUser
        every { repository.getUserById(existingId) } returns Result.success(expectedUser)

        // When
        val result = getUserByIDUseCase.getUserById(existingId)

        // Then
        assertThat(result.isSuccess).isTrue()
    }
    @Test
    fun `throws Invalid User Id Exception when id is blank`() {
        val existingId = "51"
        every { repository.getUserById(existingId)
        } returns Result.failure(InvalidUserIdException("User ID can't be empty or just digits"))

        verify(exactly = 0) { repository.getUserById(any()) }
    }

}