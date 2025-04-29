package com.berlin.data.schema

import com.berlin.data.BaseSchema
import com.berlin.model.State

class StateSchema(
    override val fileName: String,
    override val header: List<String>
) : BaseSchema<State> {
    override fun toRow(entity: State): List<String> {
        TODO("Not yet implemented")
    }

    override fun fromRow(row:List<String>): State {
        TODO("Not yet implemented")
    }

    override fun getId(entity: State): String {
        TODO("Not yet implemented")
    }

}