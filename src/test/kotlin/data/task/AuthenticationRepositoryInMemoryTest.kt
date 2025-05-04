package com.berlin.data.memory

import com.berlin.data.AuthDummyData
import com.berlin.data.authentication.AuthenticationRepositoryImpl
import com.berlin.domain.hashPassword.HashingPassword
import com.berlin.domain.hashPassword.MD5Hasher
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AuthenticationRepositoryInMemoryTest {
    private lateinit var inMemoryAuthRepositoryImpl: AuthenticationRepositoryImpl
    private lateinit var hashingPassword: HashingPassword

    @BeforeEach
    fun setup() {
        hashingPassword = MD5Hasher()
        AuthDummyData.users.clear()
        inMemoryAuthRepositoryImpl =AuthenticationRepositoryImpl(AuthDummyData)
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


}
