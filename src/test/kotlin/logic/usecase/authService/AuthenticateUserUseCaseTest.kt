package domain.logic.usecase.authService

import com.berlin.helper.AuthServiceTestData
import com.berlin.domain.logic.InvalidCredentialsException
import com.berlin.domain.logic.repositories.AuthenticationRepository
import com.google.common.truth.Truth.assertThat
import data.UserCache
import domain.usecase.authService.AuthenticateUserUseCase
import io.mockk.every
import io.mockk.mockk
import com.berlin.fakeData.FakeHashingPassword
import logic.hashPassword.HashingPassword
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
        val validUser = AuthServiceTestData.user
        val hashedPassword = hashingPassword.hashPassword(AuthServiceTestData.userPassword)

        every { authRepository.getAllUsers() } returns listOf(validUser)
        every {
            authRepository.login(
                AuthServiceTestData.userName,
                hashedPassword
            )
        } returns Result.success(validUser)

        val result = authenticateUserUseCase.login(
            AuthServiceTestData.userName,
            AuthServiceTestData.userPassword
        )

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(validUser)
    }

    @Test
    fun `login should return failure when user is not found in the repository`() {
        val hashedPassword = hashingPassword.hashPassword(AuthServiceTestData.userPassword)

        every { authRepository.getAllUsers() } returns listOf()
        every {
            authRepository.login(
                AuthServiceTestData.inValidUserName,
                hashedPassword
            )
        } returns Result.failure(InvalidCredentialsException("No found data"))

        val result = authenticateUserUseCase.login(
            AuthServiceTestData.inValidUserName,
            AuthServiceTestData.inValidUserPassword
        )

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).isEqualTo("No account")
    }

    @Test
    fun `login should return failure when username or password is empty`() {
        val hashedPassword = hashingPassword.hashPassword(AuthServiceTestData.userPassword)

        every { authRepository.getAllUsers() } returns listOf()
        every {
            authRepository.login(
                AuthServiceTestData.userNameIsEmpty,
                hashedPassword
            )
        } returns Result.failure(InvalidCredentialsException("No user found"))

        val result = authenticateUserUseCase.login(
            AuthServiceTestData.userNameIsEmpty,
            AuthServiceTestData.userPasswordIsEmpty
        )

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
        every { authRepository.getAllUsers() } returns emptyList()

        val result = authenticateUserUseCase.login(
            AuthServiceTestData.userName,
            AuthServiceTestData.userPassword
        )

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).isEqualTo("No account")
    }

    @Test
    fun `login should return failure if repository login returns failure`() {
        val userName = "Fatma"
        val password = "123456"
        val hashedPassword = "hashed_123456"
        val expectedException = InvalidCredentialsException("Invalid credentials")

        val mockedHashing = mockk<HashingPassword>()
        every { mockedHashing.hashPassword(password) } returns hashedPassword

        authenticateUserUseCase = AuthenticateUserUseCase(authRepository, mockedHashing)

        every { authRepository.getAllUsers() } returns listOf(AuthServiceTestData.user)
        UserCache.currentUser = null

        every {
            authRepository.login(userName, hashedPassword)
        } returns Result.failure(expectedException)

        val result = authenticateUserUseCase.login(userName, password)

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(expectedException)
    }
    @Test
    fun `login should return cached user even if password is wrong`() {
        val cachedUser = AuthServiceTestData.user
        every { authRepository.getAllUsers() } returns listOf(cachedUser)
        UserCache.currentUser = cachedUser

        val result = authenticateUserUseCase.login(
            cachedUser.userName,
            "wrong_password"
        )

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(cachedUser)
    }

    @Test
    fun `login should return failure if login fails with unknown exception`() {
        val user = AuthServiceTestData.user
        val hashedPassword = hashingPassword.hashPassword(AuthServiceTestData.userPassword)

        every { authRepository.getAllUsers() } returns listOf(user)
        every {
            authRepository.login(user.userName, hashedPassword)
        } returns Result.failure(RuntimeException("Unexpected error"))

        UserCache.currentUser = null

        val result = authenticateUserUseCase.login(
            user.userName,
            AuthServiceTestData.userPassword
        )

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Unexpected error")
    }

    @Test
    fun `login should not update UserCache if login fails`() {
        val user = AuthServiceTestData.user
        val hashedPassword = hashingPassword.hashPassword(AuthServiceTestData.userPassword)

        every { authRepository.getAllUsers() } returns listOf(user)
        every {
            authRepository.login(user.userName, hashedPassword)
        } returns Result.failure(InvalidCredentialsException("Wrong credentials"))

        UserCache.currentUser = null

        val result = authenticateUserUseCase.login(
            user.userName,
            AuthServiceTestData.userPassword
        )

        assertThat(result.isFailure).isTrue()
        assertThat(UserCache.currentUser).isNull()
    }
    @Test
    fun `login should return user without changing permissions if already cached`() {
        val user = AuthServiceTestData.excepctedUser
        UserCache.currentUser = user
        every { authRepository.getAllUsers() } returns listOf(user)

        val result = authenticateUserUseCase.login(user.userName, "any_password")

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()?.permission).isEqualTo(user.permission)
    }
    @Test
    fun `login should ignore cache if different username and proceed with login`() {
        val cachedUser = AuthServiceTestData.user.copy(userName = "otherUser")
        val expectedUser = AuthServiceTestData.user
        val password = AuthServiceTestData.userPassword
        val hashedPassword = hashingPassword.hashPassword(password)

        UserCache.currentUser = cachedUser
        every { authRepository.getAllUsers() } returns listOf(expectedUser)
        every { authRepository.login(expectedUser.userName, hashedPassword) } returns Result.success(expectedUser)

        val result = authenticateUserUseCase.login(expectedUser.userName, password)

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(expectedUser)
        assertThat(UserCache.currentUser).isEqualTo(expectedUser)
    }
    @Test
    fun `login should fail if password is incorrect and no cached user`() {
        val user = AuthServiceTestData.user
        val wrongPassword = "wrongPassword"
        val hashedWrongPassword = hashingPassword.hashPassword(wrongPassword)

        UserCache.currentUser = null
        every { authRepository.getAllUsers() } returns listOf(user)
        every { authRepository.login(user.userName, hashedWrongPassword) } returns Result.failure(InvalidCredentialsException("Wrong password"))

        val result = authenticateUserUseCase.login(user.userName, wrongPassword)

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(InvalidCredentialsException::class.java)
    }


}