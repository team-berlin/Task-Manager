package com.berlin.data.schema

import com.berlin.data.BaseSchema
import com.berlin.data.TaskIndex
import com.berlin.model.Task

class TaskSchema(
    override val fileName: String,
    override val header: List<String>
) : BaseSchema<Task> {

    init {
        require(fileName.isNotEmpty() && header.size == NUMBER_OF_ATTRIBUTES)
    }

    override fun toRow(entity: Task): List<String> {
        return if (checkTaskIsNotValid(entity)) emptyList()
        else taskToStringsList(entity)
    }

    override fun fromRow(row: List<String>): Task? {
        return if (checkRowIsNotValidTask(row)) null
        else stringsListToTask(row)
    }

    override fun getId(entity: Task): String? {
        return entity.id.ifEmpty { null }
    }

    private fun taskToStringsList(task: Task): List<String> {
        return listOf(
            task.id,
            task.projectId,
            task.title,
            task.description ?: "",
            task.stateId,
            task.assignedTo,
            task.createBy
        )
    }

    private fun stringsListToTask(row: List<String>): Task {
        return Task(
            id = row[TaskIndex.ID],
            projectId = row[TaskIndex.PROJECT_ID],
            title = row[TaskIndex.TITLE],
            description = row[TaskIndex.DESCRIPTION].ifEmpty { null },
            stateId = row[TaskIndex.STATE_ID],
            assignedTo = row[TaskIndex.ASSIGNED_TO],
            createBy = row[TaskIndex.CREATE_BY]
        )
    }

    private fun checkRowIsNotValidTask(row: List<String>): Boolean {
        return (row[TaskIndex.ID].isEmpty() ||
                row[TaskIndex.PROJECT_ID].isEmpty() ||
                row[TaskIndex.TITLE].isEmpty() ||
                row[TaskIndex.STATE_ID].isEmpty() ||
                row[TaskIndex.ASSIGNED_TO].isEmpty() ||
                row[TaskIndex.CREATE_BY].isEmpty())
    }

    private fun checkTaskIsNotValid(task: Task): Boolean {
        return (task.id.isEmpty() ||
                task.projectId.isEmpty() ||
                task.title.isEmpty() ||
                task.stateId.isEmpty() ||
                task.assignedTo.isEmpty() ||
                task.createBy.isEmpty())
    }

    private companion object {
        const val NUMBER_OF_ATTRIBUTES = 7
    }
}