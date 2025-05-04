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
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class GetUserByIDUseCaseTest {
    private lateinit var repository: AuthenticationRepository
    private lateinit var getUserByIDUseCase: GetUserByIDUseCase

    @BeforeEach
    fun setup() {
        repository = mockk()
        getUserByIDUseCase = GetUserByIDUseCase(repository)
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

    @ParameterizedTest
    @ValueSource(strings = [" ", "655659"])
    fun `returns failure when id is blank`(invalidString: String) {
        // When
        val result = getUserByIDUseCase.getUserById(invalidString)

        // Then
        assertThat(result.isFailure).isTrue()
        verify(exactly = 0) { repository.getUserById(any()) }
    }

}