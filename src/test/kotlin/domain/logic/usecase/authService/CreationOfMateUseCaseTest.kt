package domain.logic.usecase.authService
import domain.fakeData.FakeHashingPassword
import com.berlin.domain.logic.InvalidCredentialsException
import com.berlin.domain.logic.repositories.AuthenticationRepository
import com.berlin.domain.model.User
import com.berlin.domain.model.UserRole
import com.google.common.truth.Truth.assertThat
import domain.helper.HashingPassword
import domain.usecase.authService.CreationOfMateUseCase
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CreationOfMateUseCaseTest {

    private lateinit var authRepository: AuthenticationRepository
    private lateinit var hashingPassword: HashingPassword
    private lateinit var creationOfMateUseCase: CreationOfMateUseCase

    private val testPassword = "securePassword"
    private val hashedPassword = "hashed_$testPassword"



    @BeforeEach
    fun setup() {
        authRepository = mockk()
        hashingPassword = FakeHashingPassword()
        creationOfMateUseCase = CreationOfMateUseCase(authRepository, hashingPassword)
    }

    @Test
    fun `should return failure when username is empty`() {
        val result = creationOfMateUseCase.createMate("", testPassword)
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(InvalidCredentialsException::class.java)
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Username and password must not be empty")
    }

    @Test
    fun `should return failure when password is empty`() {
        val result = creationOfMateUseCase.createMate("validUser", "")
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(InvalidCredentialsException::class.java)
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Username and password must not be empty")
    }

    @Test
    fun `should return failure when password is less than 8 characters`() {
        val result = creationOfMateUseCase.createMate("validUser", "short")
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(InvalidCredentialsException::class.java)
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Password less than 8 characters")
    }

    @Test
    fun `should return failure when username already exists`() {
        val existingUser = User("1", "existingUser", "somePass", UserRole.MATE)
        every { authRepository.getAllUsers() } returns listOf(existingUser)

        val result = creationOfMateUseCase.createMate("existingUser", testPassword)
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(InvalidCredentialsException::class.java)
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Username already exists")
    }

    @Test
    fun `should return success when all fields are valid`() {
        every { authRepository.getAllUsers() } returns emptyList()

        val expectedUser = User("1", "newUser", hashedPassword, UserRole.MATE)
        every {
            authRepository.createMate("newUser", hashedPassword)
        } returns Result.success(expectedUser)

        val result = creationOfMateUseCase.createMate("newUser", testPassword)

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(expectedUser)
    }
}
