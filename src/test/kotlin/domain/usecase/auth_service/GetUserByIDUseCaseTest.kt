package com.berlin.domain.usecase.authService

import com.berlin.domain.exception.InvalidUserIdException
import com.berlin.domain.helper.AuthServiceTestData.existingId
import com.berlin.domain.helper.AuthServiceTestData.existingUser
import com.berlin.domain.helper.AuthServiceTestData.idExist
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


    @Test
    fun `getUserById return InvalidUserIdException when ID is empty`() {
        every {
            repository.getUserById(existingId)
        } throws InvalidUserIdException("User ID can't be empty or just digits")

        assertThrows<InvalidUserIdException> {
            getUserByIDUseCase(existingId)
        }

    }

    @Test
    fun `getUserById returns user when ID exists`() {
        every { repository.getUserById(idExist) } returns existingUser

        val result = getUserByIDUseCase(idExist)

        assertThat(result).isEqualTo(existingUser)
    }

    @Test
    fun `throws Invalid User Id Exception when id is blank`() {
        every { repository.getUserById(existingId)
        } throws InvalidUserIdException("User ID can't be empty or just digits")

        verify(exactly = 0) { repository.getUserById(any()) }
    }

}