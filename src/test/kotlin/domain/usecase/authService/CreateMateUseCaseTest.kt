//package com.berlin.domain.usecase.authService
//
//import com.berlin.domain.exception.InvalidCredentialsException
//import com.berlin.domain.usecase.utils.hash_algorithm.HashingString
//import com.berlin.domain.model.User
//import com.berlin.domain.model.UserRole
//import com.berlin.domain.repository.AuthenticationRepository
//import com.berlin.domain.usecase.utils.id_generator.IdGenerator
//import com.google.common.truth.Truth.assertThat
//import io.mockk.every
//import io.mockk.mockk
//import io.mockk.verify
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//
//class CreateMateUseCaseTest {
//
//    private lateinit var authRepository: AuthenticationRepository
//    private val idGenerator: IdGenerator = mockk()
//    private lateinit var hashingString: HashingString
//    private lateinit var createMateUseCase: CreateMateUseCase
//
//    @BeforeEach
//    fun setup() {
//        authRepository   = mockk()
//        hashingString    = mockk()
//        createMateUseCase = CreateMateUseCase(authRepository, idGenerator, hashingString)
//    }
//
//    @Test
//    fun `createMate fails when username is empty`() {
//        val result = createMateUseCase.createMate("", "validPassword")
//        assertThat(result.isFailure).isTrue()
//        assertThat(result.exceptionOrNull())
//            .isInstanceOf(InvalidCredentialsException::class.java)
//        assertThat(result.exceptionOrNull()?.message)
//            .isEqualTo("Username and password must not be empty")
//    }
//
//    @Test
//    fun `createMate fails when password is empty`() {
//        val result = createMateUseCase.createMate("validUser", "")
//        assertThat(result.isFailure).isTrue()
//        assertThat(result.exceptionOrNull())
//            .isInstanceOf(InvalidCredentialsException::class.java)
//        assertThat(result.exceptionOrNull()?.message)
//            .isEqualTo("Username and password must not be empty")
//    }
//
//    @Test
//    fun `createMate fails when password length is less than 8 characters`() {
//        val result = createMateUseCase.createMate("validUser", "short7")
//        assertThat(result.isFailure).isTrue()
//        assertThat(result.exceptionOrNull())
//            .isInstanceOf(InvalidCredentialsException::class.java)
//        assertThat(result.exceptionOrNull()?.message)
//            .isEqualTo("Password less than 8 characters")
//    }
//
//    // —— NEW TESTS BELOW ——
//
//    @Test
//    fun `createMate succeeds when credentials are valid`() {
//        // Arrange
//        val username = "validUser"
//        val password = "longEnough"
//        val fakeHash = "hashed_longEnough"
//        val fakeId   = "generated-id-123"
//        val expected = User(fakeId, username, fakeHash, UserRole.MATE)
//
//        every { hashingString.hashPassword(password)               } returns fakeHash
//        every { idGenerator.generateId(username, any(), any())     } returns fakeId
//        every { authRepository.createMate(expected)                 } returns Result.success(expected)
//
//        // Act
//        val result = createMateUseCase.createMate(username, password)
//
//        // Assert
//        assertThat(result.isSuccess).isTrue()
//        assertThat(result.getOrNull()).isEqualTo(expected)
//        verify {
//            hashingString.hashPassword(password)
//            idGenerator.generateId(username, any(), any())
//            authRepository.createMate(expected)
//        }
//    }
//
//    @Test
//    fun `createMate propagates repository failure`() {
//        // Arrange
//        val username = "validUser"
//        val password = "longEnough"
//        val fakeHash = "hashed_longEnough"
//        val fakeId   = "generated-id-456"
//        val expected = User(fakeId, username, fakeHash, UserRole.MATE)
//        val repoEx   = RuntimeException("already exists")
//
//        every { hashingString.hashPassword(password)               } returns fakeHash
//        every { idGenerator.generateId(username, any(), any())     } returns fakeId
//        every { authRepository.createMate(expected)                 } returns Result.failure(repoEx)
//
//        // Act
//        val result = createMateUseCase.createMate(username, password)
//
//        // Assert
//        assertThat(result.isFailure).isTrue()
//        assertThat(result.exceptionOrNull()).isEqualTo(repoEx)
//        verify { authRepository.createMate(expected) }
//    }
//
//}
