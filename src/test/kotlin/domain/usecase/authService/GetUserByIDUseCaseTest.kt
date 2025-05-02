package com.berlin.domain.usecase.authService
import com.berlin.domain.repository.AuthenticationRepository
import com.berlin.domain.helper.AuthServiceTestData
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
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
    fun `getUserById returns null when ID does not exist`() {
        // Given
        val nonExistentId = AuthServiceTestData.idNotExist
        every { repository.getUserById(nonExistentId) } returns null

        // When
        val result = getUserByIDUseCase.getUserById(nonExistentId)

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun `getUserById throws IllegalArgumentException when ID is empty`() {
        // When & Then
        assertThrows<IllegalArgumentException> {
            getUserByIDUseCase.getUserById("")
        }
    }

    @Test
    fun `getUserById returns user when ID exists`() {
        // Given
        val existingId = AuthServiceTestData.idExist
        val expectedUser = AuthServiceTestData.existingUser
        every { repository.getUserById(existingId) } returns expectedUser

        // When
        val result = getUserByIDUseCase.getUserById(existingId)

        // Then
        assertThat(result).isEqualTo(expectedUser)
    }

}