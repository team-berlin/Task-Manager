package com.berlin.data.memory

import com.berlin.domain.hashPassword.HashingPassword
import com.berlin.domain.hashPassword.MD5Hasher
import com.berlin.domain.model.UserRole
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AuthenticationRepositoryInMemoryTest {
    private lateinit var inMemoryAuthRepositoryImpl: AuthRepositoryInMemory
    private lateinit var hashingPassword: HashingPassword

    @BeforeEach
    fun setup() {
        hashingPassword = MD5Hasher()
        inMemoryAuthRepositoryImpl = AuthRepositoryInMemory()
    }


    @Test
    fun `login should return success when provided with valid credentials`() {
        val username = "mena"
        val password = "1234"
        inMemoryAuthRepositoryImpl.createMate(username, password)
        val result = inMemoryAuthRepositoryImpl.login(username, password)
        assertThat(result.isSuccess).isTrue()
        val user = result.getOrNull()
        assertThat(user?.userName).isEqualTo(username)
    }

    @Test
    fun `login should return failure when provided with invalid credentials`() {
        inMemoryAuthRepositoryImpl.createMate("mena", "1234")
        val result = inMemoryAuthRepositoryImpl.login("wrong", "pass")
        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `createMate should return failure when trying to create a user with an existing username`() {
        val username = "mena"
        val password = "1234"
        inMemoryAuthRepositoryImpl.createMate(username, password)
        val result = inMemoryAuthRepositoryImpl.createMate(username, password)
        assertThat(result.isFailure).isTrue()
        val exception = result.exceptionOrNull()
        assertThat(exception).isNotNull()
        assertThat(exception?.message).isEqualTo("user name is already exist")
    }
}
