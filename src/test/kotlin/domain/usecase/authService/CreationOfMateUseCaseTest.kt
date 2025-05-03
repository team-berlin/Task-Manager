package domain.logic.usecase.authService

import com.berlin.domain.hashPassword.HashingPassword
import com.berlin.domain.repository.AuthenticationRepository
import com.berlin.domain.usecase.authService.CreationOfMateUseCase
import com.berlin.domain.fakeData.FakeHashingPassword
import com.berlin.domain.helper.AuthServiceTestData
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CreationOfMateUseCaseTest {

    private lateinit var authRepository: AuthenticationRepository
    private lateinit var hashingPassword: HashingPassword
    private lateinit var createMateUseCase: CreationOfMateUseCase

    @BeforeEach
    fun setup() {
        authRepository = mockk()
        hashingPassword = FakeHashingPassword()
        createMateUseCase = CreationOfMateUseCase(authRepository, hashingPassword)
    }

    @Test
    fun `createMate fails when username is empty`() {
        // Given
        val emptyUsername = AuthServiceTestData.userNameIsEmpty
        val password = AuthServiceTestData.userPassword

        // When
        val result = createMateUseCase.createMate(emptyUsername, password)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Username and password must not be empty")
    }

    @Test
    fun `createMate fails when password is empty`() {
        // Given
        val username = AuthServiceTestData.userName
        val emptyPassword = AuthServiceTestData.userPasswordIsEmpty

        // When
        val result = createMateUseCase.createMate(username, emptyPassword)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Username and password must not be empty")
    }

    @Test
    fun `createMate fails when password length is less than 8 characters`() {
        // Given
        val username = AuthServiceTestData.userName
        val shortPassword = AuthServiceTestData.passwordLessThanEight

        // When
        val result = createMateUseCase.createMate(username, shortPassword)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Password less than 8 characters")
    }


    @Test
    fun `createMate succeeds when username and password are valid and username does not exist`() {
        // Given
        every { authRepository.getAllUsers() } returns Result.success(listOf())

        val username = AuthServiceTestData.userName
        val password = AuthServiceTestData.userPassword
        val hashedPassword = hashingPassword.hashPassword(password)

        every {
            authRepository.createMate(username, hashedPassword)
        } returns Result.success(AuthServiceTestData.excepctedUser)

        // When
        val result = createMateUseCase.createMate(username, password)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(AuthServiceTestData.excepctedUser)
    }

}
