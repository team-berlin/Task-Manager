package domain.logic.usecase.authService

import com.berlin.domain.exception.InvalidCredentialsException
import com.berlin.domain.hashPassword.HashingPassword
import com.berlin.domain.repository.AuthenticationRepository
import com.berlin.domain.fakeData.FakeHashingPassword
import com.berlin.domain.helper.AuthServiceTestData
import com.google.common.truth.Truth.assertThat
import data.UserCache
import domain.usecase.authService.AuthenticateUserUseCase
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
    fun `login returns user successfully when valid credentials are provided`() {
        // Given
        val validUser = AuthServiceTestData.user
        val hashedPassword = hashingPassword.hashPassword(AuthServiceTestData.userPassword)
        every { authRepository.getAllUsers() } returns Result.success(listOf(validUser))
        every { authRepository.login(AuthServiceTestData.userName, hashedPassword) } returns Result.success(validUser)

        // When
        val result = authenticateUserUseCase.login(AuthServiceTestData.userName, AuthServiceTestData.userPassword)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(validUser)
    }

    @Test
    fun `login fails when user is not found in repository`() {
        // Given
        val hashedPassword = hashingPassword.hashPassword(AuthServiceTestData.inValidUserPassword)
        every { authRepository.login(AuthServiceTestData.inValidUserName, hashedPassword) } returns Result.failure(
            InvalidCredentialsException("No found data")
        )
        // When
        val result = authenticateUserUseCase.login(AuthServiceTestData.inValidUserName,AuthServiceTestData.inValidUserPassword)
        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).isEqualTo("No found data")
    }

    @Test
    fun `login fails when username  is empty`() {
        // Given
        val hashedPassword = hashingPassword.hashPassword(AuthServiceTestData.userPassword)
        every { authRepository.getAllUsers() } returns Result.success(listOf())
        every { authRepository.login(AuthServiceTestData.userNameIsEmpty, hashedPassword) } returns Result.failure(
            InvalidCredentialsException("No user found")
        )
        // When
        val result = authenticateUserUseCase.login(AuthServiceTestData.userNameIsEmpty, "12345678")
        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).isEqualTo("No user found")
    }
    @Test
    fun `login fails when password is empty`() {
        // Given
        val hashedPassword = hashingPassword.hashPassword(AuthServiceTestData.userPassword)
        every { authRepository.getAllUsers() } returns Result.success(listOf())
        every { authRepository.login(AuthServiceTestData.userNameIsEmpty, hashedPassword) } returns Result.failure(
            InvalidCredentialsException("No user found")
        )

        // When
        val result = authenticateUserUseCase.login("Menna", AuthServiceTestData.userPasswordIsEmpty)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).isEqualTo("No user found")
    }

    @Test
    fun `login returns cached user when user is already authenticated`() {
        // Given
        val cachedUser = AuthServiceTestData.user
        val hashedPassword = hashingPassword.hashPassword(AuthServiceTestData.userPassword)
        UserCache.currentUser = cachedUser
        every { authRepository.getAllUsers() } returns Result.success(listOf(cachedUser))

        every { authRepository.login(AuthServiceTestData.userName, hashedPassword) } returns Result.failure(
            InvalidCredentialsException("Repository should not be called")
        )

        // When
        val result = authenticateUserUseCase.login(AuthServiceTestData.userName, AuthServiceTestData.userPassword)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(cachedUser)
    }

    @Test
    fun `login fails when repository login returns failure`() {
        // Given
        val userName = "Fatma"
        val rawPassword = "123456"
        val hashedPassword = "hashed_123456"

        val mockedHashing = mockk<HashingPassword>()
        every { mockedHashing.hashPassword(rawPassword) } returns hashedPassword

        val mockedRepo = mockk<AuthenticationRepository>()
        authenticateUserUseCase = AuthenticateUserUseCase(mockedRepo, mockedHashing)

        every { mockedRepo.getAllUsers() } returns Result.success(listOf(AuthServiceTestData.user))
        every { mockedRepo.login(userName, hashedPassword) } returns Result.failure(InvalidCredentialsException("Invalid"))

        UserCache.currentUser = null

        // When
        val result = authenticateUserUseCase.login(userName, rawPassword)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(InvalidCredentialsException::class.java)
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Invalid")
    }


    @Test
    fun `login returns cached user even if password is wrong`() {
        // Given
        val cachedUser = AuthServiceTestData.user
        UserCache.currentUser = cachedUser
        every { authRepository.getAllUsers() } returns Result.success(listOf(cachedUser))

        // When
        val result = authenticateUserUseCase.login(cachedUser.userName, "wrong_password")

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(cachedUser)
    }

    @Test
    fun `login fails with unknown exception from repository`() {
        // Given
        val user = AuthServiceTestData.user
        val hashedPassword = hashingPassword.hashPassword(AuthServiceTestData.userPassword)
        every { authRepository.getAllUsers() } returns Result.success(listOf(user))
        every { authRepository.login(user.userName, hashedPassword) } returns Result.failure(RuntimeException("Unexpected error"))
        UserCache.currentUser = null

        // When
        val result = authenticateUserUseCase.login(user.userName, AuthServiceTestData.userPassword)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Unexpected error")
    }

    @Test
    fun `UserCache remains null if login fails`() {
        // Given
        val user = AuthServiceTestData.user
        val hashedPassword = hashingPassword.hashPassword(AuthServiceTestData.userPassword)
        every { authRepository.getAllUsers() } returns Result.success(listOf(user))
        every { authRepository.login(user.userName, hashedPassword) } returns Result.failure(
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
    fun `login returns cached user with same permissions`() {
        // Given
        val user = AuthServiceTestData.excepctedUser
        UserCache.currentUser = user
        every { authRepository.getAllUsers() } returns Result.success(listOf(user))

        // When
        val result = authenticateUserUseCase.login(user.userName, "any_password")

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()?.permission).isEqualTo(user.permission)
    }

    @Test
    fun `login ignores cache if username is different and proceeds with login`() {
        // Given
        val cachedUser = AuthServiceTestData.user.copy(userName = "otherUser")
        val expectedUser = AuthServiceTestData.user
        val password = AuthServiceTestData.userPassword
        val hashedPassword = hashingPassword.hashPassword(password)

        UserCache.currentUser = cachedUser
        every { authRepository.getAllUsers() } returns Result.success(listOf(expectedUser))
        every { authRepository.login(expectedUser.userName, hashedPassword) } returns Result.success(expectedUser)

        // When
        val result = authenticateUserUseCase.login(expectedUser.userName, password)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(expectedUser)
        assertThat(UserCache.currentUser).isEqualTo(expectedUser)
    }

    @Test
    fun `login fails if password is incorrect and no cached user exists`() {
        // Given
        val user = AuthServiceTestData.user
        val wrongPassword = "wrongPassword"
        val hashedWrongPassword = hashingPassword.hashPassword(wrongPassword)

        UserCache.currentUser = null
        every { authRepository.getAllUsers() } returns Result.success(listOf(user))
        every { authRepository.login(user.userName, hashedWrongPassword) } returns Result.failure(
            InvalidCredentialsException("Wrong password")
        )

        // When
        val result = authenticateUserUseCase.login(user.userName, wrongPassword)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(InvalidCredentialsException::class.java)
    }
}
