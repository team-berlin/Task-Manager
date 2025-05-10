package com.berlin.presentation.task

import com.berlin.data.DummyData
import com.berlin.domain.exception.InputCancelledException
import com.berlin.domain.exception.InvalidAssigneeException
import com.berlin.domain.exception.InvalidSelectionException
import com.berlin.domain.usecase.task.AssignTaskUseCase
import com.berlin.domain.usecase.task.GetAllTasksUseCase
import com.berlin.presentation.UiRunner
import com.berlin.presentation.helper.choose
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer

class AssignTaskUI(
    private val assignTask: AssignTaskUseCase,
    private val getAllTasks: GetAllTasksUseCase,
    private val viewer: Viewer,
    private val reader: Reader,
) : UiRunner {

    override val id: Int = 2
    override val label: String = "Assign task"

    override suspend fun run() {
        try {
            val task = selectTask()
            val assignee = choose(
                title = "Users", elements = DummyData.users, labelOf = { it.userName }, viewer = viewer, reader = reader
            )

            assignTask(task.id, assignee.id).onSuccess { viewer.show("Assigned to ${assignee.userName}") }
                .onFailure { viewer.show(it.message ?: "Assignment failed") }

        } catch (ex: InputCancelledException) {
            viewer.show("Cancelled.")
        } catch (ex: InvalidSelectionException) {
            viewer.show("Invalid selection")
        } catch (ex: InvalidAssigneeException) {
            viewer.show("Invalid assignee")
        }
    }

    private suspend fun selectTask() = choose(
        title = "Tasks",
        elements = getAllTasks(),
        labelOf = { "${it.id} – ${it.title}" },
        viewer = viewer,
        reader = reader
    )
}
