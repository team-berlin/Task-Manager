package com.berlin.data.schema

import com.berlin.data.BaseSchema
import com.berlin.model.State

class StateSchema(
    override val fileName: String,
    override val header: List<String>
) : BaseSchema<State> {
    override fun toRow(entity: State): List<String> {
        return emptyList()
    }

    override fun fromRow(row:List<String>): State? {
        return null
    }

    override fun getId(entity: State): String {
        return ""
    }

}