package com.berlin.presentation.task

import com.berlin.domain.exception.InputCancelledException
import com.berlin.domain.exception.InvalidAssigneeException
import com.berlin.domain.exception.InvalidSelectionException
import com.berlin.domain.model.Permission
import com.berlin.domain.usecase.authService.GetAllUsersUseCase
import com.berlin.domain.usecase.task.AssignTaskUseCase
import com.berlin.domain.usecase.task.GetAllTasksUseCase
import com.berlin.presentation.PermissionedUiRunner
import com.berlin.presentation.helper.choose
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer

class AssignTaskUI(
    private val assignTask: AssignTaskUseCase,
    private val getAllTasks: GetAllTasksUseCase,
    private val getAllUsersUseCase: GetAllUsersUseCase,
    private val viewer: Viewer,
    private val reader: Reader,
) : PermissionedUiRunner {

    override val id: Int = 2
    override val label: String = "Assign task"

    override fun isAllowed(permission: Permission) = permission.assignTask

    override fun run() {
        try {
            val task = selectTask()
            val assignee = choose(
                title = "Users", elements = getAllUsersUseCase.getAllUsers(), labelOf = { it.userName }, viewer = viewer, reader = reader
            )

            assignTask(task.id, assignee.id)
            viewer.show("Assigned to ${assignee.userName}")

        } catch (ex: InputCancelledException) {
            viewer.show("Cancelled.")
        } catch (ex: InvalidSelectionException) {
            viewer.show("Invalid selection")
        } catch (ex: InvalidAssigneeException) {
            viewer.show("Invalid assignee")
        }
    }

    private fun selectTask() = choose(
        title = "Tasks",
        elements = getAllTasks(),
        labelOf = { "${it.id} – ${it.title}" },
        viewer = viewer,
        reader = reader
    )
}
