package com.berlin.presentation.auth_service

import com.berlin.domain.exception.InvalidCredentialsException
import com.berlin.domain.fakeData.FakeHashingString
import com.berlin.domain.helper.AuthServiceTestData.expectedUser
import com.berlin.domain.helper.CACHEUSER
import com.berlin.domain.helper.EMPTY_USER
import com.berlin.domain.usecase.utils.hash_algorithm.HashingString
import com.berlin.domain.model.user.User
import com.berlin.domain.repository.AuthenticationRepository
import com.berlin.presentation.authService.AuthenticateUserUI
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import com.google.common.truth.Truth.assertThat
import data.UserCache
import domain.usecase.auth_service.LoginUserUseCase
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class AuthenticateUserUITest {
    private lateinit var userCache: UserCache
    private lateinit var authenticationRepository: AuthenticationRepository
    private lateinit var hashPassword: HashingString
    private lateinit var loginUserUseCase: LoginUserUseCase
    private lateinit var authenticateUserUi: AuthenticateUserUI
    private lateinit var viewer: Viewer
    private lateinit var reader: Reader
    private val testUser = User("id123", "admin", User.UserRole.ADMIN)
    private val userName = "admin"
    private val wrongPassword = "wrong"

    @BeforeEach
    fun setup() {
        userCache = UserCache(CACHEUSER)
        authenticationRepository = mockk(relaxed = true)
        hashPassword = FakeHashingString()
        viewer = mockk(relaxed = true)
        reader = mockk()
        loginUserUseCase = LoginUserUseCase(userCache, authenticationRepository, hashPassword)
        authenticateUserUi = AuthenticateUserUI(loginUserUseCase, viewer, reader)
    }

    @Test
    fun `run should return success when user enter valid data `() {
        val hashingString = hashPassword.hashPassword("1212")
        every { reader.read() } returnsMany listOf("admin", "1212")
        every { viewer.show(any()) } just Runs
        every { authenticationRepository.login("admin", hashingString) } returns expectedUser
        val result = loginUserUseCase("admin", "1212")
        userCache.currentUser = CACHEUSER
        assertThat(result)

    }
    @Test
    fun `run should return failed when enter invalid data `() {
        every { reader.read() } returnsMany listOf("ahmed", "5684")
        every {
            authenticationRepository.login(any(), any())
        } throws  InvalidCredentialsException("No user found")

       assertThrows<InvalidCredentialsException> {
           loginUserUseCase("ahmed", "5684")
       }
    }

    @Test
    fun `login succeeds on first attempt`() {
        val expectedUser = User("user1234", "admin", User.UserRole.ADMIN)
        val rawPassword = "1212"
        val hashedPassword = hashPassword.hashPassword(rawPassword)
        every { reader.read() } returnsMany listOf("admin", "1212")
        every { authenticationRepository.login("admin", hashedPassword) } returns expectedUser

        every { viewer.show(any()) } just Runs

        authenticateUserUi.run()

       assertEquals(expectedUser, userCache.currentUser)
        verify { viewer.show("Welcome admin") }

    }

    @Test
    fun `run should login successfully on first attempt`() {
        val testUser = User("user1234", "admin", User.UserRole.ADMIN)
        val rawPassword = "1212"
        val hashedPassword = hashPassword.hashPassword(rawPassword)
        every { reader.read() } returnsMany listOf("admin", "1212")
        every { authenticationRepository.login("admin", hashedPassword) } returns testUser
        every { viewer.show(any()) } just Runs

        authenticateUserUi.run()

        Assertions.assertEquals(testUser, userCache.currentUser)
        verify { viewer.show("Welcome admin") }
    }

    @Test
    fun `should login successfully on first attempt`() {
        val rawPassword = "1234"
        val hashedPassword = hashPassword.hashPassword(rawPassword)
        every { reader.read() } returnsMany listOf("admin", "1234")
        every { authenticationRepository.login("admin", hashedPassword) } returns testUser

        authenticateUserUi.run()

        verify { viewer.show("Enter your user name: ") }
        verify { viewer.show("Enter your password: ") }
        verify { viewer.show("Welcome admin") }
    }
    @Test
    fun `should stop retrying if login succeeds before max attempts`() {
        val userCache = UserCache(EMPTY_USER)
        val hashing = mockk<HashingString>()

        val authenticateUserUseCase =
            LoginUserUseCase(userCache, authenticationRepository, hashing)
        val ui = AuthenticateUserUI(authenticateUserUseCase, viewer, reader)

        every { reader.read() } returnsMany listOf(
            userName, wrongPassword, userName, wrongPassword, userName, "correct"
        )

        every { hashing.hashPassword(wrongPassword) } returns "hashed_wrong"
        every { hashing.hashPassword("correct") } returns "hashed_correct"

        every { authenticationRepository.login(userName, "hashed_wrong") } throws InvalidCredentialsException("Invalid")
        every { authenticationRepository.login(userName, "hashed_correct") } returns testUser

        every { viewer.show(any()) } just Runs

        ui.run()

        verify { viewer.show("Welcome $userName") }
        verify(exactly = 3) { authenticationRepository.login(any(), any()) }
    }
    @Test
    fun `should handle null password`() {

        val reader = mockk<Reader>()
        val viewer = mockk<Viewer>(relaxed = true)
        val hashPassword = mockk<HashingString>()
        val authenticationRepository = mockk<AuthenticationRepository>()
        val userCache = UserCache(EMPTY_USER)

        val authenticateUserUseCase = LoginUserUseCase(userCache, authenticationRepository, hashPassword)
        val authenticateUserUi = AuthenticateUserUI(authenticateUserUseCase, viewer, reader)

        every { reader.read() } returnsMany listOf("admin", null)


        assertThrows<InvalidCredentialsException> {
            authenticateUserUi.validateUser()
        }
    }

    @Test
    fun `should handle null username and password`() {
        every { reader.read() } returnsMany listOf(null, null)
        every {
            authenticationRepository.login(
                "",
                ""
            )
        } throws InvalidCredentialsException("Invalid")

       assertThrows<InvalidCredentialsException> {
            authenticateUserUi.validateUser()

       }
    }
}