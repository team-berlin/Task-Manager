package logic.usecase

import com.berlin.AuthServiceTestData
import com.berlin.logic.InvalidCredentialsException
import com.berlin.logic.repositories.AuthenticationRepository
import com.berlin.logic.usecase.CreationOfMateUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CreationOfMateUseCaseTest {

    private lateinit var authRepository: AuthenticationRepository
    private lateinit var creationOfMateUseCase: CreationOfMateUseCase

    @BeforeEach
    fun setup() {
        authRepository = mockk()
        creationOfMateUseCase = CreationOfMateUseCase(authRepository)
    }

    @Test
    fun `createUser should throw InvalidCredentialsException when username is empty`() {
        // Given
        every {
            creationOfMateUseCase.createMate(
                AuthServiceTestData.userNameIsEmpty,
                AuthServiceTestData.userPassword
            )
        } throws InvalidCredentialsException("Name cannot be empty")

        // When & Then
        assertThrows<InvalidCredentialsException> {
            creationOfMateUseCase.createMate(
                AuthServiceTestData.userNameIsEmpty,
                AuthServiceTestData.userPassword
            )
        }
    }

    @Test
    fun `createUser should throw InvalidCredentialsException when password is empty`() {
        // Given
        every {
            creationOfMateUseCase.createMate(
                AuthServiceTestData.userName,
                AuthServiceTestData.userPasswordIsEmpty
            )
        } throws InvalidCredentialsException("Password cannot be empty")

        // When & Then
        assertThrows<InvalidCredentialsException> {
            creationOfMateUseCase.createMate(
                AuthServiceTestData.userName,
                AuthServiceTestData.userPasswordIsEmpty
            )
        }
    }

    @Test
    fun `createUser should throw InvalidCredentialsException when password is less than 8 characters`() {
        // Given
        every {
            creationOfMateUseCase.createMate(
                AuthServiceTestData.userName,
                AuthServiceTestData.passwordLessThanEight
            )
        } throws InvalidCredentialsException("Password less than 8 char")

        // When & Then
        assertThrows<InvalidCredentialsException> {
            creationOfMateUseCase.createMate(
                AuthServiceTestData.userName,
                AuthServiceTestData.passwordLessThanEight
            )
        }
    }

    @Test
    fun `createUser should throw InvalidCredentialsException when all fields are empty`() {
        // Given
        every {
            creationOfMateUseCase.createMate(
                AuthServiceTestData.userNameIsEmpty,
                AuthServiceTestData.userPasswordIsEmpty
            )
        } throws InvalidCredentialsException("Fields shouldn't be empty")

        // When & Then
        assertThrows<InvalidCredentialsException> {
            creationOfMateUseCase.createMate(
                AuthServiceTestData.userNameIsEmpty,
                AuthServiceTestData.userPasswordIsEmpty
            )
        }
    }

    @Test
    fun `createUser should return User when all fields are valid`() {
        // Given
        every {
            creationOfMateUseCase.createMate(
                AuthServiceTestData.userName,
                AuthServiceTestData.userPassword
            )
        } returns Result.success(AuthServiceTestData.user)

        // When
        val result = creationOfMateUseCase.createMate(
            AuthServiceTestData.userName,
            AuthServiceTestData.userPassword
        )

        // Then
        assertThat(result).isEqualTo(Result.success(AuthServiceTestData.user))
    }
}
