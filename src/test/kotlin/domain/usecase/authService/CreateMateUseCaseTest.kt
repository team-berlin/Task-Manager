package domain.logic.usecase.authService

import com.berlin.domain.exception.InvalidCredentialsException
import com.berlin.domain.hashPassword.HashingString
import com.berlin.domain.model.User
import com.berlin.domain.model.UserRole
import com.berlin.domain.repository.AuthenticationRepository
import com.berlin.domain.usecase.authService.CreateMateUseCase
import com.berlin.domain.usecase.utils.IDGenerator.IdGenerator
import com.google.common.base.Verify.verify
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

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
    fun `createMate succeeds when username and password are valid`() {
        val username = "validUser"
        val password = "validPassword"
        val hashedPassword = "hashedPassword"
        val generatedId = "generatedId"

        every { hashingString.hashPassword(password) } returns hashedPassword
        every { idGenerator.generateId(username) } returns generatedId
        every { authRepository.createMate(any()) } returns Result.success(User(generatedId, username, hashedPassword, UserRole.MATE))

        val result = createMateUseCase.createMate(username, password)

        assertThat(result.isSuccess).isTrue()

        val user = result.getOrNull()

        assertThat(user).isNotNull()
        assertThat(user?.id).isEqualTo(generatedId)
        assertThat(user?.userName).isEqualTo(username)
        assertThat(user?.password).isEqualTo(hashedPassword)
        assertThat(user?.role).isEqualTo(UserRole.MATE)


        verify { authRepository.createMate(
            User(
                id = generatedId,
                userName = username,
                password = hashedPassword,
                role = UserRole.MATE
            )
        )}
    }
}
