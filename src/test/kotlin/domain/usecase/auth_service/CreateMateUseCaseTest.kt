package com.berlin.domain.usecase.authService

import com.berlin.domain.exception.InvalidCredentialsException
import com.berlin.domain.helper.AuthServiceTestData.fakeId
import com.berlin.domain.helper.AuthServiceTestData.generatedId
import com.berlin.domain.helper.AuthServiceTestData.hashPassword
import com.berlin.domain.helper.AuthServiceTestData.passwordLessthanEight
import com.berlin.domain.helper.AuthServiceTestData.userName
import com.berlin.domain.helper.AuthServiceTestData.userNameIsEmpty
import com.berlin.domain.helper.AuthServiceTestData.userPassword
import com.berlin.domain.helper.AuthServiceTestData.userPasswordIsEmpty
import com.berlin.domain.model.user.User
import com.berlin.domain.repository.AuthenticationRepository
import com.berlin.domain.usecase.utils.hash_algorithm.HashingString
import com.berlin.domain.usecase.utils.id_generator.IdGenerator
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CreateMateUseCaseTest {

    private lateinit var authRepository: AuthenticationRepository
    private val idGenerator: IdGenerator = mockk()
    private lateinit var hashingString: HashingString
    private lateinit var createMateUseCase: CreateMateUseCase

    @BeforeEach
    fun setup() {
        authRepository = mockk()
        hashingString = mockk()
        createMateUseCase = CreateMateUseCase(authRepository, idGenerator, hashingString)
    }

    @Test
    fun `createMate fails when username is empty`() {
        assertThrows<InvalidCredentialsException> {
            createMateUseCase(userNameIsEmpty, userPassword)
        }
    }

    @Test
    fun `createMate fails when password is empty`() {
        assertThrows<InvalidCredentialsException> {
            createMateUseCase(userName, userPasswordIsEmpty)
        }
    }

    @Test
    fun `createMate fails when password length is less than 8 characters`() {
        assertThrows<InvalidCredentialsException> {
            createMateUseCase(userName, passwordLessthanEight)
        }
    }

    @Test
    fun `createMate returns user when credentials are valid`() {
        val expectedUser = User(generatedId, userName, User.UserRole.MATE)

        every { hashingString.hashPassword(userPassword) } returns hashPassword
        every { idGenerator.generateId(userName, any(), any()) } returns generatedId
        every { authRepository.createMate(any()) } returns expectedUser

        val result = createMateUseCase(userName, userPassword)

        assertThat(result).isEqualTo(expectedUser)
    }

    @Test
    fun `createMate propagates repository failure`() {
        val exception = InvalidCredentialsException("already exist")

        every { hashingString.hashPassword(userPassword) } returns hashPassword
        every { idGenerator.generateId(userName, any(), any()) } returns fakeId
        every { authRepository.createMate(any()) } throws exception

        val thrown = assertThrows<InvalidCredentialsException> {
            createMateUseCase(userName, userPassword)
        }

        assertThat(thrown).isEqualTo(exception)
        verify { authRepository.createMate(any()) }
    }

}
