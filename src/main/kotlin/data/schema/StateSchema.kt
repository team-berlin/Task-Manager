package com.berlin.data.schema

import com.berlin.data.BaseSchema
import com.berlin.data.StateIndex
import com.berlin.model.State

class StateSchema(
    override val fileName: String,
    override val header: List<String>
) : BaseSchema<State> {
    init {
        require(fileName.isNotEmpty() && header.size == NUMBER_OF_ATTRIBUTES)
    }

    override fun toRow(entity: State): List<String> {
        return if (
            entity.id.isEmpty() ||
            entity.name.isEmpty() ||
            entity.projectId.isEmpty()
        ) emptyList()
        else listOf(
            entity.id,
            entity.name,
            entity.projectId
        )
    }

    override fun fromRow(row: List<String>): State? {
        return if (
            row[StateIndex.ID].isEmpty() ||
            row[StateIndex.NAME].isEmpty() ||
            row[StateIndex.PROJECT_ID].isEmpty()
        ) null
        else State(
            id = row[StateIndex.ID],
            name = row[StateIndex.NAME],
            projectId = row[StateIndex.PROJECT_ID]
        )
    }

    override fun getId(entity: State): String {
        return entity.id.ifEmpty { "" }
    }

    private companion object {
        const val NUMBER_OF_ATTRIBUTES = 3
    }
}