package com.berlin.data.csv_data_source.schema

import com.berlin.data.TaskIndex
import com.berlin.data.dto.TaskDto

class TaskSchema(
    override val fileName: String,
    override val header: List<String>
) : BaseSchema<TaskDto> {

    init {
        require(fileName.isNotEmpty() && header.size == NUMBER_OF_ATTRIBUTES)
    }

    override fun toRow(entity: TaskDto): List<String> {
        return if (checkTaskIsNotValid(entity)) emptyList()
        else taskToStringsList(entity)
    }

    override fun fromRow(row: List<String>): TaskDto? {
        return if (checkRowIsNotValidTask(row)) null
        else stringsListToTask(row)
    }

    override fun getId(entity: TaskDto): String? {
        return entity.id.ifEmpty { null }
    }

    private fun taskToStringsList(task: TaskDto): List<String> {
        return listOf(
            task.id,
            task.projectId,
            task.title,
            task.description ?: "",
            task.stateId,
            task.assignedToUserId,
            task.createByUserId
        )
    }

    private fun stringsListToTask(row: List<String>): TaskDto {
        return TaskDto(
            id = row[TaskIndex.ID],
            projectId = row[TaskIndex.PROJECT_ID],
            title = row[TaskIndex.TITLE],
            description = row[TaskIndex.DESCRIPTION].ifEmpty { null },
            stateId = row[TaskIndex.STATE_ID],
            assignedToUserId = row[TaskIndex.ASSIGNED_TO_USER_ID],
            createByUserId = row[TaskIndex.CREATE_BY_USER_ID]
        )
    }

    private fun checkRowIsNotValidTask(row: List<String>): Boolean {
        return (row[TaskIndex.ID].isEmpty() ||
                row[TaskIndex.PROJECT_ID].isEmpty() ||
                row[TaskIndex.TITLE].isEmpty() ||
                row[TaskIndex.STATE_ID].isEmpty() ||
                row[TaskIndex.ASSIGNED_TO_USER_ID].isEmpty() ||
                row[TaskIndex.CREATE_BY_USER_ID].isEmpty())
    }

    private fun checkTaskIsNotValid(task: TaskDto): Boolean {
        return (task.id.isEmpty() ||
                task.projectId.isEmpty() ||
                task.title.isEmpty() ||
                task.stateId.isEmpty() ||
                task.assignedToUserId.isEmpty() ||
                task.createByUserId.isEmpty())
    }

    private companion object {
        const val NUMBER_OF_ATTRIBUTES = 7
    }
}