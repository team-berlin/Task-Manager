package com.berlin.data.mapper

import com.berlin.data.BaseDataSource
import com.berlin.data.dto.UserDto
import com.berlin.domain.exception.UserNotFoundException
import com.berlin.domain.model.User

class UserMapper(
    private val userDataSource: BaseDataSource<UserDto>,
) : EntityMapper<UserDto, User> {
    override fun mapToDomainModel(userDto: UserDto): User {
        return User(
            id = userDto.id,
            userName = userDto.userName,
            role = userDto.role
        )
    }

    override fun mapToDataModel(user: User): UserDto {
        val userDto = userDataSource.getById(user.id) ?: throw UserNotFoundException("user not found")
        return UserDto(
            id = user.id,
            userName = user.userName,
            role = user.role,
            password = userDto.password
        )
    }
}