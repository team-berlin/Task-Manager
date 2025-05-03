package com.berlin.data.memory

import com.berlin.domain.exception.UserNotFoundException
import com.berlin.domain.helper.AuthServiceTestData
import com.berlin.domain.model.UserRole
import com.google.common.truth.Truth.assertThat
import data.UserCache
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

    @Test
    fun `getUserById should return the correct user when the user exists`() {
        // Given
        val createdUser = inMemoryAuthRepositoryImpl.createMate("mena", "1234").getOrNull()!!

        // When
        val result = inMemoryAuthRepositoryImpl.getUserById(createdUser.id)

        // Then
        assertThat(result).isEqualTo(createdUser)
    }

    @Test
    fun `getUserById should return null when the user ID does not exist`() {
        // Given
        val nonExistentId = "999"

        // When
        val result = inMemoryAuthRepositoryImpl.getUserById(nonExistentId)

        // Then
        assertThat(result.onFailure { UserNotFoundException("user not found") })
    }

    @Test
    fun `getAllUsers should return all created users`() {
        // Given
        inMemoryAuthRepositoryImpl.createMate("user1", "pass1")
        inMemoryAuthRepositoryImpl.createMate("user2", "pass2")

        // When
        val users = inMemoryAuthRepositoryImpl.getAllUsers()

        // Then
        assertThat(users).hasSize(2)
    }

    @Test
    fun `getCurrentUser should return the user currently stored in the cache`() {
        // Given
        val user = AuthServiceTestData.user
        UserCache.currentUser = user

        // When
        val result = inMemoryAuthRepositoryImpl.getCurrentUser()

        // Then
        assertThat(result).isEqualTo(user)
    }
}
