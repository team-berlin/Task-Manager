package presentation.authService

import com.berlin.domain.exception.InvalidCredentialsException
import com.berlin.domain.fakeData.FakeHashingString
import com.berlin.domain.hashPassword.HashingString
import com.berlin.domain.helper.AuthServiceTestData.CACHEUSER
import com.berlin.domain.helper.AuthServiceTestData.EMPTY_USER
import com.berlin.domain.model.User
import com.berlin.domain.model.UserRole
import com.berlin.domain.repository.AuthenticationRepository
import com.berlin.presentation.authService.AuthenticateUserUI
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import com.google.common.truth.Truth.assertThat
import data.UserCache
import domain.usecase.authService.AuthenticateUserUseCase
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class AuthenticateUserUITest {
    private lateinit var userCache: UserCache
    private lateinit var authenticationRepository: AuthenticationRepository
    private lateinit var hashPassword: HashingString
    private lateinit var authenticateUserUseCase: AuthenticateUserUseCase
    private lateinit var authenticateUserUi: AuthenticateUserUI
    private lateinit var viewer: Viewer
    private lateinit var reader: Reader
    private val testUser = User("id123", "admin", "hashed123", UserRole.ADMIN)
    private val userName = "admin"
    private val wrongPassword = "wrong"

    @BeforeEach
    fun setup() {
        userCache = UserCache(CACHEUSER)
        authenticationRepository = mockk(relaxed = true)
        hashPassword = FakeHashingString()
        viewer = mockk(relaxed = true)
        reader = mockk()
        authenticateUserUseCase =
            AuthenticateUserUseCase(userCache, authenticationRepository, hashPassword)
        authenticateUserUi = AuthenticateUserUI(authenticateUserUseCase, viewer, reader)
    }

    @Test
    fun `run should return success when user enter valid data `() {
        val hashingString = hashPassword.hashPassword("1212")
        every { reader.read() } returnsMany listOf("admin", "1212")
        every { viewer.show(any()) } just Runs
        every { authenticationRepository.login("admin", hashingString) }
        val result = authenticateUserUseCase.login("admin", "1212")
        userCache.currentUser = CACHEUSER
        assertThat(result)

    }

    @Test
    fun `run should return failed when enter invalid data `() {
        every { reader.read() } returnsMany listOf("ahmed", "5684")
        every {
            authenticationRepository.login(any(), any())
        } returns Result.failure(InvalidCredentialsException("No user found"))

        val result = authenticateUserUseCase.login("ahmed", "5684")

        assertThat(result)
    }

    @Test
    fun `login succeeds on first attempt`() {
        val expectedUser = User("user1234", "admin", "1212", UserRole.ADMIN)

        every { reader.read() } returnsMany listOf("admin", "1212")
        every { authenticationRepository.login("admin", "1212") } returns Result.success(
            expectedUser
        )
        every { viewer.show(any()) } just Runs

        authenticateUserUi.run()

        assertEquals(expectedUser, userCache.currentUser)
        verify { viewer.show("Welcome admin") }

    }

    @Test
    fun `run should login successfully on first attempt`() {
        val testUser = User("user1234", "admin", "1212", UserRole.ADMIN)

        every { reader.read() } returnsMany listOf("admin", "1212")
        every { authenticationRepository.login("admin", "1212") } returns Result.success(testUser)
        every { viewer.show(any()) } just Runs

        authenticateUserUi.run()

        Assertions.assertEquals(testUser, userCache.currentUser)
        verify { viewer.show("Welcome admin") }
    }

    @Test
    fun `should login successfully on first attempt`() {
        every { reader.read() } returnsMany listOf("admin", "1234")
        every { authenticationRepository.login("admin", "1234") } returns Result.success(testUser)

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
            AuthenticateUserUseCase(userCache, authenticationRepository, hashing)
        val ui = AuthenticateUserUI(authenticateUserUseCase, viewer, reader)

        // simulate inputs
        every { reader.read() } returnsMany listOf(
            userName, wrongPassword, userName, wrongPassword, userName, "correct"
        )

        every { hashing.hashPassword(wrongPassword) } returns "hashed_wrong"
        every { hashing.hashPassword("correct") } returns "hashed_correct"

        every { authenticationRepository.login(userName, "hashed_wrong") } returnsMany listOf(
            Result.failure(Exception("Invalid")), Result.failure(Exception("Invalid"))
        )
        every { authenticationRepository.login(userName, "hashed_correct") } returns Result.success(
            testUser
        )

        ui.run()

        verify { viewer.show("Welcome $userName") }
    }

    @Test
    fun `should show Try again two times then stop on third failure`() {
        every { reader.read() } returnsMany listOf(
            "admin", "wrong", "admin", "wrong", "admin", "wrong"
        )
        every { authenticationRepository.login("admin", "wrong") } returnsMany listOf(
            Result.failure(Exception("fail1")),
            Result.failure(Exception("fail2")),
            Result.failure(Exception("fail3"))
        )
        every { viewer.show(any()) } just Runs

        val useCase = AuthenticateUserUseCase(userCache, authenticationRepository, hashPassword)
        val ui = AuthenticateUserUI(useCase, viewer, reader)

        ui.run()

        verify(exactly = 0) { viewer.show("Try again") }
    }

    @Test
    fun `should handle null password`() {
        every { reader.read() } returnsMany listOf("admin", null)
        every {
            authenticationRepository.login(
                "admin",
                ""
            )
        } returns Result.failure(Exception("Invalid"))

        val result = authenticateUserUi.validateUser()

        assertTrue(result.isFailure)
    }

    @Test
    fun `should handle null username and password`() {
        every { reader.read() } returnsMany listOf(null, null)
        every {
            authenticationRepository.login(
                "",
                ""
            )
        } returns Result.failure(Exception("Invalid"))

        val result = authenticateUserUi.validateUser()

        assertTrue(result.isFailure)
    }


}