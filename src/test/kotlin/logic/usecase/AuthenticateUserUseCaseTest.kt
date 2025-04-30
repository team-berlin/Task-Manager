package logic.usecase

import com.berlin.AuthServiceTestData
import com.berlin.data.UserCache
import com.berlin.logic.InvalidCredentialsException
import com.berlin.logic.repositories.AuthenticationRepository
import com.berlin.logic.usecase.authService.AuthenticateUserUseCase
import com.berlin.model.User
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class AuthenticateUserUseCaseTest {

    private lateinit var authRepository: AuthenticationRepository
    private lateinit var authenticateUserUseCase: AuthenticateUserUseCase

    @BeforeEach
    fun setup() {
        authRepository = mockk<AuthenticationRepository>()
        authenticateUserUseCase = AuthenticateUserUseCase(authRepository)
    }

    @Disabled
    @Test
    fun `login should return user success when user inputs valid data`() {
        val validUser = AuthServiceTestData.user
        every {
            authRepository.login(
                AuthServiceTestData.userName,
                AuthServiceTestData.userPassword
            )
        } returns Result.success(validUser)

        val result = authenticateUserUseCase.login(
            AuthServiceTestData.userName,
            AuthServiceTestData.userPassword
        )

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(validUser)
    }

    @Disabled
    @Test
    fun `login should return failure when user is not found in the repository`() {
        every {
            authRepository.login(
                AuthServiceTestData.inValidUserName,
                AuthServiceTestData.inValidUserPassword
            )
        } returns Result.failure(InvalidCredentialsException("No found data"))

        val result = authenticateUserUseCase.login(
            AuthServiceTestData.inValidUserName,
            AuthServiceTestData.inValidUserPassword
        )

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).isEqualTo("No found data")
    }

    @Test
    fun `login should return failure when username or password is empty`() {
        every {
            authRepository.login(
                AuthServiceTestData.userNameIsEmpty,
                AuthServiceTestData.userPasswordIsEmpty
            )
        } returns Result.failure(InvalidCredentialsException("No user found"))

        val result = authenticateUserUseCase.login(
            AuthServiceTestData.userNameIsEmpty,
            AuthServiceTestData.userPasswordIsEmpty
        )

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).isEqualTo("No user found")
    }

    @Disabled
    @Test
    fun `login should return user from cache if already authenticated`() {
        val cachedUser = AuthServiceTestData.user
        UserCache.currentUser = cachedUser

        every {
            authRepository.login(
                AuthServiceTestData.userName,
                AuthServiceTestData.userPassword
            )
        } returns Result.failure(InvalidCredentialsException("Repository should not be called"))

        val result = authenticateUserUseCase.login(
            AuthServiceTestData.userName,
            AuthServiceTestData.userPassword
        )

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(cachedUser)

        verify(exactly = 0) {
            authRepository.login(
                AuthServiceTestData.userName,
                AuthServiceTestData.userPassword
            )
        }
    }

    @Test
    fun `login should return failure if no users in repository`() {
        every {
            authRepository.getAllUsers()
        } returns listOf()

        val result = authenticateUserUseCase.login(
            AuthServiceTestData.userName,
            AuthServiceTestData.userPassword
        )

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).isEqualTo("List is empty")
    }
}
