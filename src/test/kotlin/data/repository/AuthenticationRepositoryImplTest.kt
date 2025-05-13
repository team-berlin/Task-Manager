package com.berlin.data.repository

import com.berlin.data.BaseDataSource
import com.berlin.data.dto.UserDto
import com.berlin.data.mapper.UserMapper
import com.berlin.data.repository.AuthenticationRepositoryImpl
import com.berlin.domain.exception.InvalidCredentialsException
import com.berlin.domain.exception.UserNotFoundException
import com.berlin.domain.model.user.User
import com.berlin.domain.model.user.UserCreation
import data.UserCache
import io.mockk.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

class AuthenticationRepositoryImplTest {

    private lateinit var userCache: UserCache
    private lateinit var userDataSource: BaseDataSource<UserDto>
    private lateinit var userMapper: UserMapper
    private lateinit var repository: AuthenticationRepositoryImpl

    @BeforeEach
    fun setUp() {
        userCache = mockk(relaxed = true)
        userDataSource = mockk(relaxed = true)
        userMapper = mockk(relaxed = true)

        repository = AuthenticationRepositoryImpl(userCache, userDataSource, userMapper)
    }

    @Test
    fun `login should return a User when valid credentials are provided`() {
        val userDto = UserDto("1", "JohnDoe", "password", User.UserRole.MATE)
        val user = User("1", "JohnDoe", User.UserRole.MATE)

        every { userDataSource.getAll() } returns listOf(userDto)
        every { userMapper.mapToDomainModel(userDto) } returns user

        val result = repository.login("JohnDoe", "password")

        assertEquals(user, result)
    }

    @Test
    fun `login should throw InvalidCredentialsException when invalid credentials are provided`() {
        every { userDataSource.getAll() } returns emptyList()

        assertThrows<InvalidCredentialsException> {
            repository.login("JohnDoe", "wrongPassword")
        }
    }


    @Test
    fun `createMate should throw exception if writing user to data source fails`() {
        val userCreation = UserCreation("1", "JohnDoe", User.UserRole.MATE, "hashedPassword",)
        val userDto = UserDto("1", "JohnDoe", "hashedPassword", User.UserRole.MATE)

        every { userDataSource.write(userDto) } throws Exception("Write failed")

        assertThrows<Exception> {
            repository.createMate(userCreation)
        }
    }

    @Test
    fun `getUserById should return User when valid userId is provided`() {
        val userDto = UserDto("1", "JohnDoe", "password", User.UserRole.MATE)
        val user = User("1", "JohnDoe", User.UserRole.MATE)

        every { userDataSource.getById("1") } returns userDto
        every { userMapper.mapToDomainModel(userDto) } returns user

        val result = repository.getUserById("1")

        assertEquals(user, result)
    }

    @Test
    fun `getUserById should throw UserNotFoundException when userId is not found`() {
        every { userDataSource.getById("1") } returns null

        assertThrows<UserNotFoundException> {
            repository.getUserById("1")
        }
    }

    @Test
    fun `getAllUsers should return a list of Users`() {
        val userDto1 = UserDto("1", "JohnDoe", "password", User.UserRole.MATE)
        val userDto2 = UserDto("2", "JaneDoe", "password", User.UserRole.MATE)
        val user1 = User("1", "JohnDoe", User.UserRole.MATE)
        val user2 = User("2", "JaneDoe", User.UserRole.MATE)

        every { userDataSource.getAll() } returns listOf(userDto1, userDto2)
        every { userMapper.mapToDomainModel(userDto1) } returns user1
        every { userMapper.mapToDomainModel(userDto2) } returns user2

        val result = repository.getAllUsers()

        assertEquals(listOf(user1, user2), result)
    }

    @Test
    fun `getAllUsers should return an empty list if no users exist`() {
        every { userDataSource.getAll() } returns emptyList()

        val result = repository.getAllUsers()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `getCurrentUser should return the current User from the cache`() {
        val user = User("1", "JohnDoe", User.UserRole.MATE)

        every { userCache.currentUser } returns user

        val result = repository.getCurrentUser()

        assertEquals(user, result)
    }
}
