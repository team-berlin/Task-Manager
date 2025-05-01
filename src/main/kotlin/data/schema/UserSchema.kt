package com.berlin.data.schema

import com.berlin.data.BaseSchema
import com.berlin.data.UserIndex
import com.berlin.model.User
import com.berlin.model.UserRole

class UserSchema(
    override val fileName: String,
    override val header: List<String>
) : BaseSchema<User> {

    init {
        require(fileName.isNotEmpty()&&header.size==NUMBER_OF_ATTRIBUTES)
    }

    override fun toRow(entity: User): List<String> {
        return if (checkUserIsNotValid(entity)) emptyList()
        else userToStringsList(entity)
    }

    override fun fromRow(row: List<String>): User? {
        return if (checkRowIsNotValidUser(row)) null
        else stringsListToUser(row)
    }

    override fun getId(entity: User): String? {
        return entity.id.ifEmpty { null }
    }

    private fun userToStringsList(user: User): List<String> {
        return listOf(
            user.id,
            user.userName,
            user.password,
            user.role.toString()
        )
    }

    private fun stringsListToUser(row: List<String>): User {
        return User(
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

    private fun checkUserIsNotValid(user: User): Boolean {
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