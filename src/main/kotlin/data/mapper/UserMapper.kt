package com.berlin.data.mapper

import com.berlin.data.BaseDataSource
import com.berlin.data.dto.UserDto
import com.berlin.domain.exception.UserNotFoundException
import com.berlin.domain.model.user.User

class UserMapper(
    private val userDataSource: BaseDataSource<UserDto>,
) : EntityMapper<UserDto, User> {

    override fun mapToDomainModel(from: UserDto): User {
        return User(
            id = from.id,
            userName = from.userName,
            role = from.role
        )
    }

    override fun mapToDataModel(from: User): UserDto {
        val userDto = userDataSource.getById(from.id)
            ?: throw UserNotFoundException("user not found")
        return UserDto(
            id = from.id,
            userName = from.userName,
            role = from.role,
            password = userDto.password
        )
    }
}