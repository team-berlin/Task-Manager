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
        return if (
            entity.id.isEmpty()||
            entity.userName.isEmpty()||
            entity.password.isEmpty()
            ) emptyList()

        else listOf(
            entity.id,
            entity.userName,
            entity.password,
            entity.role.toString(),
        )

    }

    override fun fromRow(row:List<String>): User? {
        return if (
            row[UserIndex.ID].isEmpty()||
            row[UserIndex.USER_NAME].isEmpty()||
            row[UserIndex.PASSWORD].isEmpty()||
            row[UserIndex.ROLE] !in enumValues<UserRole>().map { it.name }

        ) null

        else User(
            id = row[UserIndex.ID],
            userName = row[UserIndex.USER_NAME],
            password = row[UserIndex.PASSWORD],
            role = when(row[UserIndex.ROLE]){
                UserRole.ADMIN.toString()->UserRole.ADMIN
                UserRole.MATE.toString()->UserRole.MATE
                else ->UserRole.MATE
            }
        )
    }


    override fun getId(entity: User): String {

        return entity.id.ifEmpty { "" }
    }
    private companion object{
        const val NUMBER_OF_ATTRIBUTES=4
    }

}