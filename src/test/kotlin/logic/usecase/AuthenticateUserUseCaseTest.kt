package logic.usecase

import com.berlin.AuthServiceTestData
import com.berlin.logic.InvalidCredentialsException
import com.berlin.logic.repositories.AuthenticationRepository
import com.berlin.logic.usecase.AuthenticateUserUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class AuthenticateUserUseCaseTest {

    private lateinit var authRepository: AuthenticationRepository
    private lateinit var authenticateUserUseCase: AuthenticateUserUseCase

    @BeforeEach
    fun setup() {
        authRepository = mockk()
        authenticateUserUseCase = AuthenticateUserUseCase(authRepository)
    }

    @Test
    fun `login should return user success when user inputs valid data`() {
        // Given
        every {
            authenticateUserUseCase.login(
                AuthServiceTestData.userName,
                AuthServiceTestData.userPassword
            )
        } returns Result.success(AuthServiceTestData.user)

        // When
        val result = authenticateUserUseCase.login(
            AuthServiceTestData.userName,
            AuthServiceTestData.userPassword
        )

        // Then
        assertThat(result).isEqualTo(Result.success(AuthServiceTestData.user))
    }

    @Test
    fun `login should throw InvalidCredentialsException when fields are empty`() {
        // Given
        every {
            authenticateUserUseCase.login(
                AuthServiceTestData.userNameIsEmpty,
                AuthServiceTestData.userPasswordIsEmpty
            )
        } throws InvalidCredentialsException("Fields can't be empty")

        // When & Then
        assertThrows<InvalidCredentialsException> {
            authenticateUserUseCase.login(
                AuthServiceTestData.userNameIsEmpty,
                AuthServiceTestData.userPasswordIsEmpty
            )
        }
    }

    @Test
    fun `login should throw InvalidCredentialsException when credentials are invalid`() {
        // Given
        every {
            authenticateUserUseCase.login(
                AuthServiceTestData.inValidUserName,
                AuthServiceTestData.inValidUserPassword
            )
        } throws InvalidCredentialsException("No found data")

        // When & Then
        assertThrows<InvalidCredentialsException> {
            authenticateUserUseCase.login(
                AuthServiceTestData.inValidUserName,
                AuthServiceTestData.inValidUserPassword
            )
        }
    }
}
