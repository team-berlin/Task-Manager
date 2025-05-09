package domain.logic.usecase.authService

import com.berlin.domain.exception.InvalidCredentialsException
import com.berlin.domain.hashPassword.HashingString
import com.berlin.domain.model.User
import com.berlin.domain.model.UserRole
import com.berlin.domain.repository.AuthenticationRepository
import com.berlin.domain.usecase.authService.CreateMateUseCase
import com.berlin.domain.usecase.utils.IDGenerator.IdGenerator
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CreateMateUseCaseTest {

    private lateinit var authRepository: AuthenticationRepository
    private val idGenerator: IdGenerator = mockk()
    private lateinit var hashingString: HashingString
    private lateinit var createMateUseCase: CreateMateUseCase

    @BeforeEach
    fun setup() {
        authRepository = mockk()
        hashingString = mockk()
       // idGenerator = mockk()
        createMateUseCase = CreateMateUseCase(authRepository, idGenerator, hashingString)
    }

    @Test
    fun `createMate fails when username is empty`() {
        val emptyUsername = ""
        val password = "validPassword"

        val result = createMateUseCase.createMate(emptyUsername, password)

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Username and password must not be empty")
    }

    @Test
    fun `createMate fails when password is empty`() {
        val username = "validUser"
        val emptyPassword = ""

        val result = createMateUseCase.createMate(username, emptyPassword)

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Username and password must not be empty")
    }

    @Test
    fun `createMate fails when password length is less than 8 characters`() {
        val username = "validUser"
        val shortPassword = "12345"

        val result = createMateUseCase.createMate(username, shortPassword)

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Password less than 8 characters")
    }

    @Test
    fun `createMate fails with empty password`() {
        val result = createMateUseCase.createMate("user", "")
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(InvalidCredentialsException::class.java)
    }

    @Test
    fun `createMate fails with empty username`() {
        val result = createMateUseCase.createMate("", "password123")
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(InvalidCredentialsException::class.java)
    }

    @Test
    fun `createMate fails with password exactly 7 characters`() {
        val result = createMateUseCase.createMate("user", "passwor")
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(InvalidCredentialsException::class.java)
    }
    @Test
    fun `createMate should fail if password is less than 8 characters`() {
        val userName = "mateUser"
        val password = "short"

        val result = createMateUseCase.createMate(userName, password)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is InvalidCredentialsException)
        assertEquals("Password less than 8 characters", result.exceptionOrNull()?.message)
    }
    @Test
    fun `createMate should hash the password and create user with correct details`() {
        val userName = "mateUser"
        val password = "validPassword"
        val hashedPassword = "hashedPassword"
        val generatedId = "12345"

        every { hashingString.hashPassword(password) } returns hashedPassword // Ensure the password is hashed
        every { idGenerator.generateId(userName) } returns generatedId // Ensure the ID is generated
        every { authRepository.createMate(any()) } returns Result.success(User(generatedId, userName, hashedPassword, UserRole.MATE))

        val result = createMateUseCase.createMate(userName, password)

        assertTrue(result.isSuccess)
        val user = result.getOrNull()

        assertNotNull(user)
        assertEquals(user?.userName, userName)
        assertEquals(user?.password, hashedPassword)
        assertEquals(user?.role, UserRole.MATE)


        verify {
            authRepository.createMate(
                User(
                    id = generatedId,
                    userName = userName,
                    password = hashedPassword,
                    role = UserRole.MATE
                )
            )
        }
    }

    @Test
    fun `createMate should fail if password is too short`() {
        val userName = "mateUser"
        val password = "short"

        val result = createMateUseCase.createMate(userName, password)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is InvalidCredentialsException)
        assertEquals("Password less than 8 characters", result.exceptionOrNull()?.message)

        verify(exactly = 0) { authRepository.createMate(any()) }
    }

    }

