package com.berlin.domain.usecase.authService
import com.berlin.domain.exception.InvalidCredentialsException
import com.berlin.domain.hashPassword.HashingString
import com.berlin.domain.repository.AuthenticationRepository
import com.berlin.domain.fakeData.FakeHashingString
import com.berlin.domain.helper.AuthServiceTestData
import com.berlin.domain.helper.AuthServiceTestData.CACHEUSER
import com.berlin.domain.helper.AuthServiceTestData.EMPTY_USER
import com.berlin.domain.helper.AuthServiceTestData.userName
import com.berlin.domain.helper.AuthServiceTestData.userPassword
import com.berlin.domain.model.User
import com.berlin.domain.model.UserRole
import com.google.common.truth.Truth.assertThat
import data.UserCache
import domain.usecase.authService.AuthenticateUserUseCase
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AuthenticateUserUseCaseTest {

    private lateinit var authRepository: AuthenticationRepository
    private lateinit var hashingString: HashingString
    private lateinit var authenticateUserUseCase: AuthenticateUserUseCase
    private var cashedUser = CACHEUSER
    private lateinit var userCache: UserCache

    @BeforeEach
    fun setup() {
        authRepository = mockk<AuthenticationRepository>()
        hashingString = FakeHashingString()
        userCache= UserCache(cashedUser)
        authenticateUserUseCase = AuthenticateUserUseCase(userCache,authRepository, hashingString)
    }
    @Test
    fun `login returns user successfully when valid credentials are provided`() {
        // Given
        val validUser = AuthServiceTestData.user
        val hashedPassword = hashingString.hashPassword(AuthServiceTestData.userPassword)
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
        val hashedPassword = hashingString.hashPassword(AuthServiceTestData.inValidUserPassword)
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
        val hashedPassword = hashingString.hashPassword(AuthServiceTestData.userPassword)
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
        val hashedPassword = hashingString.hashPassword(AuthServiceTestData.userPassword)
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
        val hashedPassword = hashingString.hashPassword(AuthServiceTestData.userPassword)
        userCache.currentUser = cachedUser
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

        val mockedHashing = mockk<HashingString>()
        every { mockedHashing.hashPassword(rawPassword) } returns hashedPassword

        val mockedRepo = mockk<AuthenticationRepository>()
        authenticateUserUseCase = AuthenticateUserUseCase(userCache,mockedRepo, mockedHashing)

        every { mockedRepo.getAllUsers() } returns Result.success(listOf(AuthServiceTestData.user))
        every { mockedRepo.login(userName, hashedPassword) } returns Result.failure(InvalidCredentialsException("Invalid"))

        userCache.currentUser = cashedUser

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
        userCache.currentUser = cachedUser
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
        val hashedPassword = hashingString.hashPassword(AuthServiceTestData.userPassword)
        every { authRepository.getAllUsers() } returns Result.success(listOf(user))
        every { authRepository.login(user.userName, hashedPassword) } returns Result.failure(RuntimeException("Unexpected error"))
        userCache.currentUser = cashedUser

        // When
        val result = authenticateUserUseCase.login(user.userName, AuthServiceTestData.userPassword)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Unexpected error")
    }

    @Test
    fun `login ignores cache if username is different and proceeds with login`() {
        // Given
        val cachedUser = AuthServiceTestData.user.copy(userName = "otherUser")
        val expectedUser = AuthServiceTestData.user
        val password = AuthServiceTestData.userPassword
        val hashedPassword = hashingString.hashPassword(password)

        userCache.currentUser = cachedUser
        every { authRepository.getAllUsers() } returns Result.success(listOf(expectedUser))
        every { authRepository.login(expectedUser.userName, hashedPassword) } returns Result.success(expectedUser)

        // When
        val result = authenticateUserUseCase.login(expectedUser.userName, password)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(expectedUser)
        assertThat(userCache.currentUser).isEqualTo(expectedUser)
    }

    @Test
    fun `login fails if password is incorrect and no cached user exists`() {
        // Given
        val user = AuthServiceTestData.user
        val wrongPassword = "wrongPassword"
        val hashedWrongPassword = hashingString.hashPassword(wrongPassword)

        userCache.currentUser = cashedUser
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
  @Test
  fun `userCashing returns null when user enter invalid data`(){
      //Given
      val emptyUser =EMPTY_USER
      userCache.currentUser = emptyUser
      val hashPassword = hashingString.hashPassword(userPassword)
      every { authRepository.getAllUsers() } returns Result.success(listOf(emptyUser))
      every { authRepository.login(any() , any()) } returns Result.failure(
          InvalidCredentialsException("No user found")
      )
      //When
      val result = authenticateUserUseCase.login(userName, hashPassword)
      //Then
      assertTrue(result.isFailure)
     assertThat(result.exceptionOrNull() is InvalidCredentialsException)
  }
    @Test
    fun `login should return failure when repository returns failure`() {
        val userCache = UserCache(EMPTY_USER)
        val repository = mockk<AuthenticationRepository>()
        val hashingString = FakeHashingString()

        val useCase = AuthenticateUserUseCase(userCache, repository, hashingString)

        val userName = "admin"
        val password = "wrongPass"
        val expectedException = InvalidCredentialsException("Invalid login")

        every { repository.login(userName, any()) } returns Result.failure(expectedException)
        val result = useCase.login(userName, password)

        assertTrue(result.isFailure)
        Assertions.assertEquals(expectedException, result.exceptionOrNull())
    }
}
