package com.berlin.data.csv_data_source.schema

import com.berlin.data.StateIndex
import com.berlin.domain.model.TaskState

class StateSchema(
    override val fileName: String,
    override val header: List<String>
) : BaseSchema<TaskState> {

    init {
        require(fileName.isNotEmpty() && header.size == NUMBER_OF_ATTRIBUTES)
    }

    override fun toRow(entity: TaskState): List<String> {
        return if (checkStateIsNotValid(entity)) emptyList()
        else stateToStringsList(entity)
    }

    override fun fromRow(row: List<String>): TaskState? {
        return if (checkRowIsNotValidState(row)) null
        else stringsListToState(row)
    }

    override fun getId(entity: TaskState): String? {
        return entity.id.ifEmpty { null }
    }

    private fun stateToStringsList(state: TaskState): List<String> {
        return listOf(
            state.id,
            state.name,
            state.projectId
        )
    }

    private fun stringsListToState(row: List<String>): TaskState {
        return TaskState(
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

    private fun checkStateIsNotValid(state: TaskState): Boolean {
        return (state.id.isEmpty() ||
                state.name.isEmpty() ||
                state.projectId.isEmpty())
    }

    private companion object {
        const val NUMBER_OF_ATTRIBUTES = 3
    }
}