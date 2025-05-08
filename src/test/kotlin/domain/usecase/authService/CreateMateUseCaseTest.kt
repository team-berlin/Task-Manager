package domain.logic.usecase.authService

import com.berlin.domain.hashPassword.HashingString
import com.berlin.domain.repository.AuthenticationRepository
import com.berlin.domain.usecase.authService.CreateMateUseCase
import com.berlin.domain.helper.AuthServiceTestData
import com.berlin.domain.usecase.utils.IDGenerator.IdGenerator
import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CreateMateUseCaseTest {

    private lateinit var authRepository: AuthenticationRepository
    private lateinit var idGenerator: IdGenerator
    private lateinit var hashingString: HashingString
    private lateinit var createMateUseCase: CreateMateUseCase

    @BeforeEach
    fun setup() {
        authRepository = mockk()
        hashingString = mockk()
        idGenerator = mockk()
        createMateUseCase = CreateMateUseCase(authRepository, idGenerator, hashingString)
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

}


