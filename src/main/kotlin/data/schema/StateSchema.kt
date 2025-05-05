package com.berlin.data.schema

import com.berlin.data.BaseSchema
import com.berlin.data.StateIndex
import com.berlin.domain.model.State

class StateSchema(
    override val fileName: String,
    override val header: List<String>
) : BaseSchema<State> {

    init {
        require(fileName.isNotEmpty() && header.size == NUMBER_OF_ATTRIBUTES)
    }

    override fun toRow(entity: State): List<String> {
        return if (checkStateIsNotValid(entity)) emptyList()
        else stateToStringsList(entity)
    }

    override fun fromRow(row: List<String>): State? {
        return if (checkRowIsNotValidState(row)) null
        else stringsListToState(row)
    }

    override fun getId(entity: State): String? {
        return entity.id.ifEmpty { null }
    }

    private fun stateToStringsList(state: State): List<String> {
        return listOf(
            state.id,
            state.name,
            state.projectId
        )
    }

    private fun stringsListToState(row: List<String>): State {
        return State(
            id = row[StateIndex.ID],
            name = row[StateIndex.NAME],
            projectId = row[StateIndex.PROJECT_ID]
        )
    }

    private fun checkRowIsNotValidState(row: List<String>): Boolean {
        return (row[StateIndex.ID].isEmpty() ||
                row[StateIndex.NAME].isEmpty() ||
                row[StateIndex.PROJECT_ID].isEmpty())
    }

    private fun checkStateIsNotValid(state: State): Boolean {
        return (state.id.isEmpty() ||
                state.name.isEmpty() ||
                state.projectId.isEmpty())
    }

    private companion object {
        const val NUMBER_OF_ATTRIBUTES = 3
    }
}