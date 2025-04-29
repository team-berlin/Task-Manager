package com.berlin.data.schema

import com.berlin.data.BaseSchema
import com.berlin.model.User

class UserSchema(
    override val fileName: String,
    override val header: List<String>
) : BaseSchema<User> {
    override fun toRow(entity: User): List<String> {
        TODO("Not yet implemented")
    }

    override fun fromRow(row:List<String>): User {
        TODO("Not yet implemented")
    }

    override fun getId(entity: User): String {
        TODO("Not yet implemented")
    }

}