package com.berlin.data.schema

import com.berlin.data.BaseSchema
import com.berlin.model.User

class UserSchema(
    override val fileName: String,
    override val header: List<String>
) : BaseSchema<User> {
    override fun toRow(entity: User): List<String> {
        return emptyList()
    }

    override fun fromRow(row:List<String>): User? {
        return null
    }

    override fun getId(entity: User): String {
        return ""
    }

}