package com.berlin.data.repository

import com.berlin.data.BaseDataSource
import com.berlin.data.dto.UserDto
import com.berlin.data.mapper.UserMapper
import com.berlin.domain.exception.InvalidCredentialsException
import com.berlin.domain.exception.UserNotFoundException
import com.berlin.domain.model.user.User
import com.berlin.domain.model.user.UserCreation
import com.berlin.domain.repository.AuthenticationRepository
import data.UserCache

class AuthenticationRepositoryImpl(
    private val userCache: UserCache,
    private val userDataSource: BaseDataSource<UserDto>,
    private val userMapper: UserMapper
) : AuthenticationRepository {

    override fun login(userName: String, password: String): User {
        val user = userDataSource.getAll().find { it.userName == userName && it.password == password }
        return if (user != null) {
            userMapper.mapToDomainModel(user)
        } else {
            throw InvalidCredentialsException("Invalid credentials")
        }
    }

    override fun createMate(user: UserCreation): User {
        val userDto = UserDto(
            id = user.id,
            userName = user.userName,
            password = user.hashedPassword,
            role = user.role
        )
        userDataSource.write(userDto)
        return userMapper.mapToDomainModel(userDto)
    }

    override fun getUserById(userId: String): User {
        return userDataSource.getById(userId)?.let {
            userMapper.mapToDomainModel(it)
        } ?: throw UserNotFoundException(userId)
    }

    override fun getAllUsers(): List<User> {
        return userDataSource.getAll().map {
            userMapper.mapToDomainModel(it)
        }
    }

    override fun getCurrentUser(): User {
        val user = userCache.currentUser
        return user
    }
}