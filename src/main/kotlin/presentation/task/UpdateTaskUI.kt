package com.berlin.presentation.task

import com.berlin.data.DummyData
import com.berlin.domain.exception.InputCancelledException
import com.berlin.domain.exception.InvalidSelectionException
import com.berlin.domain.exception.InvalidTaskTitle
import com.berlin.domain.exception.TaskNotFoundException
import com.berlin.domain.usecase.task.GetAllTasksUseCase
import com.berlin.domain.usecase.task.UpdateTaskUseCase
import com.berlin.presentation.UiRunner
import com.berlin.presentation.helper.choose
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer

class UpdateTaskUI(
    private val updateTask: UpdateTaskUseCase,
    private val getAllTasks: GetAllTasksUseCase,
    private val viewer: Viewer,
    private val reader: Reader
) : UiRunner {

    override val id: Int = 5
    override val label: String = "Update task"

    override fun run() {
        try {
            val task = choose(
                title    = "Tasks to update",
                elements = getAllTasks(),
                labelOf  = { "${it.id} – ${it.title}" },
                viewer   = viewer,
                reader   = reader
            )

            viewer.show("Enter new title (blank to keep “${task.title}”):")
            val rawTitle = reader.read()?.trim().orEmpty()
            val newTitle = rawTitle.ifBlank { null }

            viewer.show("Enter new description (blank to keep):")
            val rawDesc = reader.read()?.trim()
            val newDesc = rawDesc?.ifBlank { null }

            viewer.show("Select new assignee (or X to keep ${task.assignedToUserId}):")
            val newAssigneeId = try {
                val user = choose(
                    title    = "Users",
                    elements = DummyData.users,
                    labelOf  = { it.userName },
                    viewer   = viewer,
                    reader   = reader
                )
                user.id
            } catch (ex: InputCancelledException) {
                null
            }

            updateTask(
                task.id,
                title            = newTitle,
                description      = newDesc,
                assignedToUserId = newAssigneeId
            )
                .onSuccess { viewer.show("Task updated: ${it.id}") }
                .onFailure { viewer.show(it.message ?: "Update failed") }

        } catch (ex: InputCancelledException) {
            viewer.show("Cancelled.")
        } catch (ex: InvalidSelectionException) {
            viewer.show("Invalid selection")
        } catch (ex: InvalidTaskTitle) {
            viewer.show("Invalid task title")
        } catch (ex: TaskNotFoundException) {
            viewer.show("Task not founc")
        }
    }
}
