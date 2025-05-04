package com.berlin.domain.usecase.authService

import com.berlin.domain.exception.InvalidCredentialsException
import com.berlin.domain.helper.AuthServiceTestData
import com.berlin.domain.repository.AuthenticationRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
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

//    @Test
//    fun `getUserById returns User Not Valid Exception when ID does not exist`() {
//        // Given
//        val nonExistentId = idNotExist
//        every { repository.getUserById(nonExistentId) } returns Result.failure(UserNotFoundException(idNotExist))
//
//        // When
//        val result = getUserByIDUseCase.getUserById(nonExistentId)
//
//        // Then
//        assertThat(result.isFailure).isTrue()
//        assertThat(result.exceptionOrNull()).isEqualTo( UserNotFoundException(idNotExist))
//    }

    @Test
    fun `getUserById throws InvalidUserIdException when ID is empty`() {
        // When & Then
        assertThrows<InvalidCredentialsException> {
            getUserByIDUseCase.getUserById("")
        }
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
        assertThrows<InvalidCredentialsException> {
            getUserByIDUseCase.getUserById("   ")
        }
        verify(exactly = 0) { repository.getUserById(any()) }
    }
}