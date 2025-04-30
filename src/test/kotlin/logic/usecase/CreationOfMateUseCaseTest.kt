package logic.usecase

import com.berlin.AuthServiceTestData
import com.berlin.logic.repositories.AuthenticationRepository
import com.berlin.logic.usecase.authService.CreationOfMateUseCase
import com.berlin.model.User
import com.berlin.model.UserRole
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CreationOfMateUseCaseTest {

    private lateinit var authRepository: AuthenticationRepository
    private lateinit var creationOfMateUseCase: CreationOfMateUseCase

    @BeforeEach
    fun setup() {
        authRepository = mockk()
        creationOfMateUseCase = CreationOfMateUseCase(authRepository)
    }

    @Test
    fun `createUser should return failure when username is empty`() {
        every {
            authRepository.getAllUsers()
        } returns listOf()

        val result = creationOfMateUseCase.createMate(
            AuthServiceTestData.userNameIsEmpty,
            AuthServiceTestData.userPassword
        )

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Username and password must not be empty")
    }

    @Test
    fun `createUser should return failure when password is empty`() {
        every {
            authRepository.getAllUsers()
        } returns listOf()

        val result = creationOfMateUseCase.createMate(
            AuthServiceTestData.userName,
            AuthServiceTestData.userPasswordIsEmpty
        )

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Username and password must not be empty")
    }

    @Test
    fun `createUser should return failure when password is less than 8 characters`() {
        every {
            authRepository.getAllUsers()
        } returns listOf()

        val result = creationOfMateUseCase.createMate(
            AuthServiceTestData.userName,
            AuthServiceTestData.passwordLessThanEight
        )

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Password less than 8 characters")
    }

    @Test
    fun `createUser should return failure when all fields are empty`() {
        every {
            authRepository.getAllUsers()
        } returns listOf()

        val result = creationOfMateUseCase.createMate(
            AuthServiceTestData.userNameIsEmpty,
            AuthServiceTestData.userPasswordIsEmpty
        )

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Username and password must not be empty")
    }

    @Test
    fun `createUser should return failure when username already exists`() {
        val existingUser = User("1", "existingUser", "password", UserRole.MATE)
        every {
            authRepository.getAllUsers()
        } returns listOf(existingUser)

        val result = creationOfMateUseCase.createMate(
            "existingUser",
            "newPassword"
        )

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Username already exists")
    }

    @Test
    fun `createUser should return User when all fields are valid`() {
        every {
            authRepository.getAllUsers()
        } returns listOf()

        every {
            authRepository.createMate(AuthServiceTestData.userName, AuthServiceTestData.userPassword)
        } returns Result.success(User("1", AuthServiceTestData.userName, AuthServiceTestData.userPassword, UserRole.MATE))

        val result = creationOfMateUseCase.createMate(
            AuthServiceTestData.userName,
            AuthServiceTestData.userPassword
        )

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(User("1", AuthServiceTestData.userName, AuthServiceTestData.userPassword, UserRole.MATE))
    }
}
