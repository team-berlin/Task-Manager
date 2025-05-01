package logic.usecase.authService

import logic.fakeData.FakeHashingPassword
import com.berlin.domain.logic.InvalidCredentialsException
import com.berlin.domain.logic.repositories.AuthenticationRepository
import logic.helper.HashingPassword
import com.google.common.truth.Truth.assertThat
import data.UserCache
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AuthenticateUserUseCaseTest {

    private lateinit var authRepository: AuthenticationRepository
    private lateinit var hashingPassword: HashingPassword
    private lateinit var authenticateUserUseCase: AuthenticateUserUseCase

    @BeforeEach
    fun setup() {
        authRepository = mockk<AuthenticationRepository>()
        hashingPassword = FakeHashingPassword()
        authenticateUserUseCase = AuthenticateUserUseCase(authRepository, hashingPassword)

    }


    @Test
    fun `login should return user success when user inputs valid data`() {
        //Given
        val validUser = AuthServiceTestData.user
        val hashedPassword = hashingPassword.hashPassword(AuthServiceTestData.userPassword)

        every { authRepository.getAllUsers() } returns listOf(validUser)

        every {
            authRepository.login(
                AuthServiceTestData.userName,
                hashedPassword
            )
        } returns Result.success(validUser)

        //When
        val result = authenticateUserUseCase.login(
            AuthServiceTestData.userName,
            AuthServiceTestData.userPassword
        )

        //Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(validUser)
    }


    @Test
    fun `login should return failure when user is not found in the repository`() {
        //Given
        val hashedPassword = hashingPassword.hashPassword(AuthServiceTestData.userPassword)
        every { authRepository.getAllUsers() } returns listOf()
        every {
            authRepository.login(
                AuthServiceTestData.inValidUserName,
                hashedPassword
            )
        } returns Result.failure(InvalidCredentialsException("No found data"))
        //When
        val result = authenticateUserUseCase.login(
            AuthServiceTestData.inValidUserName,
            AuthServiceTestData.inValidUserPassword
        )

        //Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).isEqualTo("No account")
    }

    @Test
    fun `login should return failure when username or password is empty`() {
        //Given
        val hashedPassword = hashingPassword.hashPassword(AuthServiceTestData.userPassword)
        every { authRepository.getAllUsers() } returns listOf()
        every {
            authRepository.login(
                AuthServiceTestData.userNameIsEmpty,
                hashedPassword
            )
        } returns Result.failure(InvalidCredentialsException("No user found"))

        //When
        val result = authenticateUserUseCase.login(
            AuthServiceTestData.userNameIsEmpty,
            AuthServiceTestData.userPasswordIsEmpty
        )

        //Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).isEqualTo("No user found")
    }


    @Test
    fun `login should return user from cache when already authenticated`() {
        val cachedUser = AuthServiceTestData.user
        val hashedPassword = hashingPassword.hashPassword(AuthServiceTestData.userPassword)
        UserCache.currentUser = cachedUser

        every { authRepository.getAllUsers() } returns listOf(cachedUser)

        every {
            authRepository.login(
                AuthServiceTestData.userName,
                hashedPassword
            )
        } returns Result.failure(InvalidCredentialsException("Repository should not be called"))

        val result = authenticateUserUseCase.login(
            AuthServiceTestData.userName,
            AuthServiceTestData.userPassword
        )

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(cachedUser)


    }

    @Test
    fun `login should return failure when no users in repository`() {
        every {
            authRepository.getAllUsers()
        } returns emptyList()

        val result = authenticateUserUseCase.login(
            AuthServiceTestData.userName,
            AuthServiceTestData.userPassword
        )

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).isEqualTo("No account")
    }
}
