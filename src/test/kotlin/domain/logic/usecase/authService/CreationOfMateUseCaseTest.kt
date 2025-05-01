package domain.logic.usecase.authService

import AuthServiceTestData
import com.berlin.domain.logic.repositories.AuthenticationRepository
import com.google.common.truth.Truth.assertThat
import domain.fakeData.FakeHashingPassword
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

    @BeforeEach
    fun setup() {
        authRepository = mockk()
        hashingPassword = FakeHashingPassword()
        creationOfMateUseCase = CreationOfMateUseCase(authRepository, hashingPassword)
    }

    @Test
    fun `createMate should return failure when username is empty`() {
        val result = creationOfMateUseCase.createMate(
            AuthServiceTestData.userNameIsEmpty, AuthServiceTestData.userPassword
        )
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Username and password must not be empty")
    }

    @Test
    fun `createMate should return failure when password is empty`() {
        val result = creationOfMateUseCase.createMate(
            AuthServiceTestData.userName, AuthServiceTestData.userPasswordIsEmpty
        )
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Username and password must not be empty")
    }

    @Test
    fun `createMate should return failure when password is less than 8 characters`() {
        val result = creationOfMateUseCase.createMate(
            AuthServiceTestData.userName, AuthServiceTestData.passwordLessThanEight
        )
        assertThat(result.isFailure).isTrue()

        assertThat(result.exceptionOrNull()?.message).isEqualTo("Password less than 8 characters")
    }

    @Test
    fun `createMate should return failure when username already exists`() {
        val existingUser = AuthServiceTestData.user
        every { authRepository.getAllUsers() } returns listOf(existingUser)

        val result = creationOfMateUseCase.createMate(
            AuthServiceTestData.userName, AuthServiceTestData.userPassword
        )
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Username already exists")
    }

    @Test
    fun `createMate should return success when all fields are valid`() {
        every { authRepository.getAllUsers() } returns emptyList()
        every {
            authRepository.createMate(
                AuthServiceTestData.userName,
                hashingPassword.hashPassword(AuthServiceTestData.userPassword)
            )
        } returns Result.success(AuthServiceTestData.excepctedUser)

        val result = creationOfMateUseCase.createMate(
            AuthServiceTestData.userName, AuthServiceTestData.userPassword
        )

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(AuthServiceTestData.excepctedUser)
    }

}
