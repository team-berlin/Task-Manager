package com.berlin.data.memory

import com.berlin.data.AuthDummyData
import com.berlin.data.authentication.AuthenticationRepositoryImpl
import com.berlin.domain.hashPassword.HashingString
import com.berlin.domain.hashPassword.MD5Hasher
import com.berlin.domain.helper.AuthServiceTestData
import com.google.common.truth.Truth.assertThat
import data.UserCache
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AuthenticationRepositoryInMemoryTest {
    private lateinit var inMemoryAuthRepositoryImpl: AuthenticationRepositoryImpl
    private lateinit var hashingString: HashingString

    @BeforeEach
    fun setup() {
        hashingString = MD5Hasher()
        AuthDummyData.users.clear()
        inMemoryAuthRepositoryImpl =AuthenticationRepositoryImpl(userCache = UserCache(),AuthDummyData)
    }



    @Test
    fun `login should return success when provided with valid credentials`() {
        val username = "Fatma"
        val password = "hashed_securePassword"
        inMemoryAuthRepositoryImpl.createMate(AuthServiceTestData.excepctedUser)
        val result = inMemoryAuthRepositoryImpl.login(username, password)
        assertThat(result.isSuccess).isTrue()
        val user = result.getOrNull()
        assertThat(user?.userName).isEqualTo(username)
    }

    @Test
    fun `login should return failure when provided with invalid credentials`() {
        inMemoryAuthRepositoryImpl.createMate(AuthServiceTestData.user)
        val result = inMemoryAuthRepositoryImpl.login("wrong", "pass")
        assertThat(result.isFailure).isTrue()
    }


}
