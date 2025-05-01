package com.berlin.data.memory

import com.berlin.domain.helper.AuthServiceTestData
import com.berlin.domain.model.UserRole
import com.google.common.truth.Truth.assertThat
import data.UserCache
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class InMemoryAuthRepositoryImplTest{
    private lateinit var inMemoryAuthRepositoryImpl: InMemoryAuthRepositoryImpl
        @BeforeEach
        fun setup() {
            inMemoryAuthRepositoryImpl = InMemoryAuthRepositoryImpl()
        }

        @Test
        fun `createMate should return a new user with correct role`() {
            val result = inMemoryAuthRepositoryImpl.createMate("mena", "1234")
            assertThat(result.isSuccess).isTrue()
            val user = result.getOrNull()
            assertThat(user?.userName).isEqualTo("mena")
            assertThat(user?.role).isEqualTo(UserRole.MATE)
        }

        @Test
        fun `login should succeed when credentials are correct`() {
            inMemoryAuthRepositoryImpl.createMate("mena", "1234")
            val result = inMemoryAuthRepositoryImpl.login("mena", "1234")
            assertThat(result.isSuccess).isTrue()
            assertThat(result.getOrNull()?.userName).isEqualTo("mena")
        }

        @Test
        fun `login should fail when credentials are incorrect`() {
            inMemoryAuthRepositoryImpl.createMate("mena", "1234")
            val result = inMemoryAuthRepositoryImpl.login("wrong", "pass")
            assertThat(result.isFailure).isTrue()
        }

        @Test
        fun `getUserById should return correct user`() {
            val created = inMemoryAuthRepositoryImpl.createMate("mena", "1234").getOrNull()
            val result = inMemoryAuthRepositoryImpl.getUserById(created!!.id)
            assertThat(result).isEqualTo(created)
        }

        @Test
        fun `getUserById should return null when id not found`() {
            val result = inMemoryAuthRepositoryImpl.getUserById("999")
            assertThat(result).isNull()
        }

        @Test
        fun `getAllUsers should return all users`() {
            inMemoryAuthRepositoryImpl.createMate("user1", "pass1")
            inMemoryAuthRepositoryImpl.createMate("user2", "pass2")
            val users = inMemoryAuthRepositoryImpl.getAllUsers()
            assertThat(users).hasSize(2)
        }

        @Test
        fun `getCurrentUser should return user from cache`() {
            val user = AuthServiceTestData.user
            UserCache.currentUser = user
            assertThat(inMemoryAuthRepositoryImpl.getCurrentUser()).isEqualTo(user)
        }
    }
