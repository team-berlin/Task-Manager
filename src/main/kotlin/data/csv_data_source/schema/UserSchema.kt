package com.berlin.data.csv_data_source.schema

import com.berlin.data.UserIndex
import com.berlin.data.dto.UserDto
import com.berlin.domain.model.UserRole

class UserSchema(
    override val fileName: String,
    override val header: List<String>
) : BaseSchema<UserDto> {

    init {
        require(fileName.isNotEmpty()&&header.size== NUMBER_OF_ATTRIBUTES)
    }

    override fun toRow(entity: UserDto): List<String> {
        return if (checkUserIsNotValid(entity)) emptyList()
        else userToStringsList(entity)
    }

    override fun fromRow(row: List<String>): UserDto? {
        return if (checkRowIsNotValidUser(row)) null
        else stringsListToUser(row)
    }

    override fun getId(entity: UserDto): String? {
        return entity.id.ifEmpty { null }
    }

    private fun userToStringsList(user: UserDto): List<String> {
        return listOf(
            user.id,
            user.userName,
            user.password,
            user.role.toString()
        )
    }

    private fun stringsListToUser(row: List<String>): UserDto{
        return UserDto(
            id = row[UserIndex.ID],
            userName = row[UserIndex.USER_NAME],
            password = row[UserIndex.PASSWORD],
            role = stringToUserRole(row[UserIndex.ROLE])
        )
    }

    private fun checkRowIsNotValidUser(row: List<String>): Boolean {
        return (row[UserIndex.ID].isEmpty() ||
                row[UserIndex.USER_NAME].isEmpty() ||
                row[UserIndex.PASSWORD].isEmpty() ||
                row[UserIndex.ROLE] !in enumValues<UserRole>().map { it.name })
    }

    private fun checkUserIsNotValid(user: UserDto): Boolean {
        return (user.id.isEmpty() ||
                user.userName.isEmpty() ||
                user.password.isEmpty())
    }

    private fun stringToUserRole(roleString: String): UserRole {
        return when (roleString) {
            UserRole.ADMIN.toString() -> UserRole.ADMIN
            UserRole.MATE.toString() -> UserRole.MATE
            else -> UserRole.MATE
        }
    }

    private companion object {
        const val NUMBER_OF_ATTRIBUTES = 4
    }
}