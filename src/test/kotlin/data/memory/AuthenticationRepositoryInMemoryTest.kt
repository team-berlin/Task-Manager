package com.berlin.data.memory

import com.berlin.domain.model.UserRole
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class AuthenticationRepositoryInMemoryTest {
    private lateinit var inMemoryAuthRepositoryImpl: AuthRepositoryInMemory

    @BeforeEach
    fun setup() {
        inMemoryAuthRepositoryImpl = AuthRepositoryInMemory()
    }

    @Test
    fun `createMate should return a user with the correct username and MATE role when creation succeeds`() {
        // Given
        val username = "mena"
        val password = "1234"

        // When
        val result = inMemoryAuthRepositoryImpl.createMate(username, password)

        // Then
        assertThat(result.isSuccess).isTrue()
        val user = result.getOrNull()
        assertThat(user?.userName).isEqualTo(username)
        assertThat(user?.role).isEqualTo(UserRole.MATE)
    }

    @Test
    fun `login should return success when provided with valid credentials`() {
        // Given
        val username = "mena"
        val password = "1234"
        inMemoryAuthRepositoryImpl.createMate(username, password)

        // When
        val result = inMemoryAuthRepositoryImpl.login(username, password)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()?.userName).isEqualTo(username)
    }

    @Test
    fun `login should return failure when provided with invalid credentials`() {
        // Given
        inMemoryAuthRepositoryImpl.createMate("mena", "1234")

        // When
        val result = inMemoryAuthRepositoryImpl.login("wrong", "pass")

        // Then
        assertThat(result.isFailure).isTrue()
    }

}