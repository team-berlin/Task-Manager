package com.berlin.domain.usecase.authService

import com.berlin.domain.exception.InvalidCredentialsException
import com.berlin.domain.fakeData.FakeHashingString
import com.berlin.domain.helper.AuthServiceTestData
import com.berlin.domain.helper.AuthServiceTestData.hashPassword
import com.berlin.domain.helper.AuthServiceTestData.testUserPassword
import com.berlin.domain.helper.AuthServiceTestData.userName
import com.berlin.domain.helper.AuthServiceTestData.userPassword
import com.berlin.domain.helper.AuthServiceTestData.userPasswordIsEmpty
import com.berlin.domain.helper.CACHEUSER
import com.berlin.domain.helper.EMPTY_USER
import com.berlin.domain.repository.AuthenticationRepository
import com.berlin.domain.usecase.utils.hash_algorithm.HashingString
import com.google.common.truth.Truth.assertThat
import data.UserCache
import domain.usecase.auth_service.LoginUserUseCase
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class AuthenticateUserUseCaseTest {

    private lateinit var authRepository: AuthenticationRepository
    private lateinit var hashingString: HashingString
    private lateinit var loginUserUseCase: LoginUserUseCase
    private var cashedUser = CACHEUSER
    private lateinit var userCache: UserCache

    @BeforeEach
    fun setup() {
        authRepository = mockk<AuthenticationRepository>()
        hashingString = FakeHashingString()
        userCache = UserCache(cashedUser)
        loginUserUseCase = LoginUserUseCase(userCache, authRepository, hashingString)
    }

    @Test
    fun `login returns user successfully when valid credentials are provided`() {
        val validUser = AuthServiceTestData.user
        val hashedPassword = hashingString.hashPassword(userPassword)
        every { authRepository.getAllUsers() } returns listOf(validUser)
        every { authRepository.login(userName, hashedPassword) } returns validUser

        val result = loginUserUseCase(userName, userPassword)

        assertThat(result).isNotNull()
        assertThat(result).isEqualTo(validUser)
    }

    @Test
    fun `login fails when user is not found in repository`() {
        val hashedPassword = hashingString.hashPassword(AuthServiceTestData.inValidUserPassword)
        every {
            authRepository.login(
                AuthServiceTestData.inValidUserName,
                hashedPassword
            )
        } throws InvalidCredentialsException("No user found")

        assertThrows<InvalidCredentialsException> {
            loginUserUseCase(
                AuthServiceTestData.inValidUserName,
                AuthServiceTestData.inValidUserPassword
            )
        }
    }

    @Test
    fun `login fails when username  is empty`() {
        val hashedPassword = hashingString.hashPassword(userPassword)
        every { authRepository.getAllUsers() } returns emptyList()
        every {
            authRepository.login(
                AuthServiceTestData.userNameIsEmpty,
                hashedPassword
            )
        } throws InvalidCredentialsException("No user found")

        assertThrows<InvalidCredentialsException> {
            loginUserUseCase(AuthServiceTestData.userNameIsEmpty, userPassword)
        }
    }


    @Test
    fun `login fails when password is empty`() {
        val hashedPassword = hashingString.hashPassword(userPassword)
        every { authRepository.getAllUsers() } returns listOf()
        every {
            authRepository.login(
                AuthServiceTestData.userNameIsEmpty,
                hashedPassword
            )
        } throws InvalidCredentialsException("No user found")

        assertThrows<InvalidCredentialsException> {
            loginUserUseCase("Menna", AuthServiceTestData.userPasswordIsEmpty)
        }
    }

    @Test
    fun `login throws exception when credentials are invalid even if cache has user`() {
        val cachedUser = AuthServiceTestData.user
        val hashedPassword = hashingString.hashPassword(userPassword)
        userCache.currentUser = cachedUser
        every { authRepository.getAllUsers() } returns listOf(cachedUser)

        every {
            authRepository.login(userName, hashedPassword)
        } throws InvalidCredentialsException("Repository should not be called")

        assertThrows<InvalidCredentialsException> {
            loginUserUseCase(userName, userPasswordIsEmpty)
        }
    }

    @Test
    fun `login fails when repository login returns failure`() {
        val userName = userName
        val rawPassword = testUserPassword
        val hashedPassword = hashPassword

        val mockedHashing = mockk<HashingString>()
        every { mockedHashing.hashPassword(rawPassword) } returns hashedPassword

        val mockedRepo = mockk<AuthenticationRepository>()
        loginUserUseCase = LoginUserUseCase(userCache, mockedRepo, mockedHashing)

        every { mockedRepo.getAllUsers() } returns listOf(AuthServiceTestData.user)
        every {
            mockedRepo.login(
                userName,
                hashedPassword
            )
        } throws InvalidCredentialsException("Invalid")

        userCache.currentUser = cashedUser

        assertThrows<InvalidCredentialsException> {
            loginUserUseCase(userName, rawPassword)
        }
    }

    @Test
    fun `login returns cached user even if password is wrong`() {
        val cachedUser = AuthServiceTestData.user
        val hashedPassword = hashingString.hashPassword(userPassword)
        userCache.currentUser = cachedUser
        every { authRepository.login(any(), any()) } returns cachedUser

        val result = loginUserUseCase(userName, hashedPassword)

        assertThat(result).isEqualTo(cachedUser)
    }

    @Test
    fun `login fails with unknown exception from repository`() {

        val user = AuthServiceTestData.user
        val hashedPassword = hashingString.hashPassword(userPassword)
        every { authRepository.getAllUsers() } returns listOf(user)
        every {
            authRepository.login(
                user.userName,
                hashedPassword
            )
        } throws InvalidCredentialsException("No found user ")
        userCache.currentUser = cashedUser

        assertThrows<InvalidCredentialsException> {
            loginUserUseCase(user.userName, userPassword)
        }
    }

    @Test
    fun `login ignores cache if username is different and proceeds with login`() {
        val cachedUser = AuthServiceTestData.user.copy(userName = "otherUser")
        val expectedUser = AuthServiceTestData.user
        val password = userPassword
        val hashedPassword = hashingString.hashPassword(password)

        userCache.currentUser = cachedUser
        every { authRepository.getAllUsers() } returns listOf(expectedUser)
        every { authRepository.login(expectedUser.userName, hashedPassword) } returns expectedUser

        val result = loginUserUseCase(expectedUser.userName, password)

        assertThat(result).isEqualTo(expectedUser)
        assertThat(userCache.currentUser).isEqualTo(expectedUser)
    }


    @Test
    fun `login fails if password is incorrect and no cached user exists`() {
        val user = AuthServiceTestData.user
        val wrongPassword = userPassword
        val hashedWrongPassword = hashingString.hashPassword(wrongPassword)

        userCache.currentUser = cashedUser
        every { authRepository.getAllUsers() } returns listOf(user)
        every {
            authRepository.login(user.userName, hashedWrongPassword)
        } throws InvalidCredentialsException("Wrong password")
        assertThrows<InvalidCredentialsException> {
            loginUserUseCase(user.userName, wrongPassword)
        }

    }

    @Test
    fun `userCashing returns null when user enter invalid data`() {
        val emptyUser = EMPTY_USER
        userCache.currentUser = emptyUser
        val hashPassword = hashingString.hashPassword(userPassword)
        every { authRepository.getAllUsers() } returns listOf(emptyUser)
        every { authRepository.login(any(), any())
        } throws InvalidCredentialsException("No user found")


        assertThrows<InvalidCredentialsException> {
            loginUserUseCase(userName, hashPassword)
        }
    }

    @Test
    fun `login should return failure when repository throws exception`() {
        val userCache = UserCache(EMPTY_USER)
        val repository = mockk<AuthenticationRepository>()
        val hashingString = FakeHashingString()
        val loginUserUseCase = LoginUserUseCase(userCache, repository, hashingString)

        every {
            repository.login(userName, any())
        } throws InvalidCredentialsException("Invalid login")

        assertThrows<InvalidCredentialsException> {
            loginUserUseCase(userName, userPassword)
        }
    }
}