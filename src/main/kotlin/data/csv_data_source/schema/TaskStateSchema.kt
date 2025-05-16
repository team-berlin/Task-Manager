package com.berlin.data.csv_data_source.schema

import com.berlin.data.StateIndex
import com.berlin.data.dto.TaskStateDto

class TaskStateSchema(
    override val fileName: String, override val header: List<String>
) : BaseSchema<TaskStateDto> {

    init {
        require(fileName.isNotEmpty() && header.size == NUMBER_OF_ATTRIBUTES)
    }

    override fun toRow(entity: TaskStateDto): List<String> {
        return if (checkTaskStateDtoIsNotValid(entity).not()) {
            mapTaskStateDtoToList(entity)
        } else {
            emptyList()
        }
    }

    override fun fromRow(row: List<String>): TaskStateDto? {
        return if (checkRowIsNotValidTaskStateDto(row).not()) {
            mapListToTaskStateDto(row)
        } else {
            null
        }
    }

    override fun getId(entity: TaskStateDto): String? {
        return entity.id.ifEmpty { null }
    }

    private fun mapTaskStateDtoToList(state: TaskStateDto): List<String> {
        return listOf(
            state.id, state.name, state.projectId
        )
    }

    private fun mapListToTaskStateDto(row: List<String>): TaskStateDto {
        return TaskStateDto(
            id = row[StateIndex.ID], name = row[StateIndex.NAME], projectId = row[StateIndex.PROJECT_ID]
        )
    }

    private fun checkRowIsNotValidTaskStateDto(row: List<String>): Boolean {
        return (row[StateIndex.ID].isEmpty() || row[StateIndex.NAME].isEmpty() || row[StateIndex.PROJECT_ID].isEmpty())
    }

    private fun checkTaskStateDtoIsNotValid(state: TaskStateDto): Boolean {
        return (state.id.isEmpty() || state.name.isEmpty() || state.projectId.isEmpty())
    }

    private companion object {
        const val NUMBER_OF_ATTRIBUTES = 3
    }
}