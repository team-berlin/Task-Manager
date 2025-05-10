package domain.logic.usecase.authService

import com.berlin.domain.exception.InvalidCredentialsException
import com.berlin.domain.hashPassword.HashingPassword
import com.berlin.domain.repository.AuthenticationRepository
import com.berlin.domain.fakeData.FakeHashingPassword
import com.berlin.domain.helper.AuthServiceTestData
import com.google.common.truth.Truth.assertThat
import data.UserCache
import domain.usecase.authService.AuthenticateUserUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
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
    fun `login returns user successfully when valid credentials are provided`() = runTest {
        // Given
        val validUser = AuthServiceTestData.user
        val hashedPassword = hashingPassword.hashPassword(AuthServiceTestData.userPassword)
        coEvery { authRepository.getAllUsers() } returns Result.success(listOf(validUser))
        coEvery { authRepository.login(AuthServiceTestData.userName, hashedPassword) } returns Result.success(validUser)

        // When
        val result = authenticateUserUseCase.login(AuthServiceTestData.userName, AuthServiceTestData.userPassword)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(validUser)
    }

    @Test
    fun `login fails when user is not found in repository`() = runTest {
        // Given
        val hashedPassword = hashingPassword.hashPassword(AuthServiceTestData.inValidUserPassword)
        coEvery { authRepository.login(AuthServiceTestData.inValidUserName, hashedPassword) } returns Result.failure(
            InvalidCredentialsException("No found data")
        )
        // When
        val result = authenticateUserUseCase.login(AuthServiceTestData.inValidUserName,AuthServiceTestData.inValidUserPassword)
        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).isEqualTo("No found data")
    }

    @Test
    fun `login fails when username  is empty`()= runTest  {
        // Given
        val hashedPassword = hashingPassword.hashPassword(AuthServiceTestData.userPassword)
        coEvery { authRepository.getAllUsers() } returns Result.success(listOf())
        coEvery { authRepository.login(AuthServiceTestData.userNameIsEmpty, hashedPassword) } returns Result.failure(
            InvalidCredentialsException("No user found")
        )
        // When
        val result = authenticateUserUseCase.login(AuthServiceTestData.userNameIsEmpty, "12345678")
        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).isEqualTo("No user found")
    }
    @Test
    fun `login fails when password is empty`() = runTest {
        // Given
        val hashedPassword = hashingPassword.hashPassword(AuthServiceTestData.userPassword)
        coEvery { authRepository.getAllUsers() } returns Result.success(listOf())
        coEvery { authRepository.login(AuthServiceTestData.userNameIsEmpty, hashedPassword) } returns Result.failure(
            InvalidCredentialsException("No user found")
        )

        // When
        val result = authenticateUserUseCase.login("Menna", AuthServiceTestData.userPasswordIsEmpty)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).isEqualTo("No user found")
    }

    @Test
    fun `login returns cached user when user is already authenticated`()= runTest {
        // Given
        val cachedUser = AuthServiceTestData.user
        val hashedPassword = hashingPassword.hashPassword(AuthServiceTestData.userPassword)
        UserCache.currentUser = cachedUser
        coEvery { authRepository.getAllUsers() } returns Result.success(listOf(cachedUser))

        coEvery { authRepository.login(AuthServiceTestData.userName, hashedPassword) } returns Result.failure(
            InvalidCredentialsException("Repository should not be called")
        )

        // When
        val result = authenticateUserUseCase.login(AuthServiceTestData.userName, AuthServiceTestData.userPassword)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(cachedUser)
    }

    @Test
    fun `login fails when repository login returns failure`() = runTest{
        // Given
        val userName = "Fatma"
        val rawPassword = "123456"
        val hashedPassword = "hashed_123456"

        val mockedHashing = mockk<HashingPassword>()
        coEvery { mockedHashing.hashPassword(rawPassword) } returns hashedPassword

        val mockedRepo = mockk<AuthenticationRepository>()
        authenticateUserUseCase = AuthenticateUserUseCase(mockedRepo, mockedHashing)

        coEvery { mockedRepo.getAllUsers() } returns Result.success(listOf(AuthServiceTestData.user))
        coEvery { mockedRepo.login(userName, hashedPassword) } returns Result.failure(InvalidCredentialsException("Invalid"))

        UserCache.currentUser = null

        // When
        val result = authenticateUserUseCase.login(userName, rawPassword)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(InvalidCredentialsException::class.java)
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Invalid")
    }


    @Test
    fun `login returns cached user even if password is wrong`() = runTest{
        // Given
        val cachedUser = AuthServiceTestData.user
        UserCache.currentUser = cachedUser
        coEvery { authRepository.getAllUsers() } returns Result.success(listOf(cachedUser))

        // When
        val result = authenticateUserUseCase.login(cachedUser.userName, "wrong_password")

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(cachedUser)
    }

    @Test
    fun `login fails with unknown exception from repository`() = runTest {
        // Given
        val user = AuthServiceTestData.user
        val hashedPassword = hashingPassword.hashPassword(AuthServiceTestData.userPassword)
        coEvery { authRepository.getAllUsers() } returns Result.success(listOf(user))
        coEvery { authRepository.login(user.userName, hashedPassword) } returns Result.failure(RuntimeException("Unexpected error"))
        UserCache.currentUser = null

        // When
        val result = authenticateUserUseCase.login(user.userName, AuthServiceTestData.userPassword)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Unexpected error")
    }

    @Test
    fun `UserCache remains null if login fails`() = runTest {
        // Given
        val user = AuthServiceTestData.user
        val hashedPassword = hashingPassword.hashPassword(AuthServiceTestData.userPassword)
        coEvery { authRepository.getAllUsers() } returns Result.success(listOf(user))
        coEvery { authRepository.login(user.userName, hashedPassword) } returns Result.failure(
            InvalidCredentialsException("Wrong credentials")
        )
        UserCache.currentUser = null

        // When
        val result = authenticateUserUseCase.login(user.userName, AuthServiceTestData.userPassword)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(UserCache.currentUser).isNull()
    }


    @Test
    fun `login ignores cache if username is different and proceeds with login`() = runTest {
        // Given
        val cachedUser = AuthServiceTestData.user.copy(userName = "otherUser")
        val expectedUser = AuthServiceTestData.user
        val password = AuthServiceTestData.userPassword
        val hashedPassword = hashingPassword.hashPassword(password)

        UserCache.currentUser = cachedUser
        coEvery { authRepository.getAllUsers() } returns Result.success(listOf(expectedUser))
        coEvery { authRepository.login(expectedUser.userName, hashedPassword) } returns Result.success(expectedUser)

        // When
        val result = authenticateUserUseCase.login(expectedUser.userName, password)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(expectedUser)
        assertThat(UserCache.currentUser).isEqualTo(expectedUser)
    }

    @Test
    fun `login fails if password is incorrect and no cached user exists`() = runTest {
        // Given
        val user = AuthServiceTestData.user
        val wrongPassword = "wrongPassword"
        val hashedWrongPassword = hashingPassword.hashPassword(wrongPassword)

        UserCache.currentUser = null
        coEvery { authRepository.getAllUsers() } returns Result.success(listOf(user))
        coEvery { authRepository.login(user.userName, hashedWrongPassword) } returns Result.failure(
            InvalidCredentialsException("Wrong password")
        )

        // When
        val result = authenticateUserUseCase.login(user.userName, wrongPassword)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(InvalidCredentialsException::class.java)
    }
}
