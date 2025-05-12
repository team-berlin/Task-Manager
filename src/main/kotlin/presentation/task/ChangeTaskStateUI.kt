package com.berlin.presentation.task

import com.berlin.domain.exception.InputCancelledException
import com.berlin.domain.exception.InvalidSelectionException
import com.berlin.domain.exception.InvalidTaskStateException
import com.berlin.domain.exception.TaskNotFoundException
import com.berlin.domain.model.Permission
import com.berlin.domain.usecase.state.GetAllStatesUseCase
import com.berlin.domain.usecase.task.ChangeTaskStateUseCase
import com.berlin.domain.usecase.task.GetAllTasksUseCase
import com.berlin.presentation.PermissionedUiRunner
import com.berlin.presentation.helper.choose
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer

class ChangeTaskStateUI(
    private val changeState: ChangeTaskStateUseCase,
    private val getAllTasks: GetAllTasksUseCase,
    private val getAllStates: GetAllStatesUseCase,
    private val viewer: Viewer,
    private val reader: Reader,
) : PermissionedUiRunner {

    override val id: Int = 6
    override val label: String = "Change task state"

    override fun isAllowed(permission: Permission) = permission.changeTaskState

    override fun run() {
        try {
            val task = choose(
                title = "Tasks",
                elements = getAllTasks(),
                labelOf = { "${it.id} – ${it.title} [${it.stateId}]" },
                viewer = viewer,
                reader = reader
            )

            val possible = getAllStates().filter { it.projectId == task.projectId }
            if (possible.isEmpty()) {
                viewer.show("No states defined for project ${task.projectId}")
                return
            }
            val state = choose(
                title = "States for project ${task.projectId}",
                elements = possible,
                labelOf = { (it).name },
                viewer = viewer,
                reader = reader
            )

            val updatedTask = changeState(task.id, state.id)
            viewer.show("Task ${updatedTask.id} moved to ${state.name}")

        } catch (ex: InputCancelledException) {
            viewer.show("Cancelled.")
        } catch (ex: InvalidSelectionException) {
            viewer.show("Invalid selection")
        } catch (ex: InvalidTaskStateException) {
            viewer.show("Invalid task state")
        } catch (ex: TaskNotFoundException) {
            viewer.show("Task not found")
        }
    }
}
