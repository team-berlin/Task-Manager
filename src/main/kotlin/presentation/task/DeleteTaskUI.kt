package com.berlin.presentation.task

import com.berlin.domain.exception.InputCancelledException
import com.berlin.domain.exception.InvalidSelectionException
import com.berlin.domain.exception.InvalidTaskIdException
import com.berlin.domain.model.Permission
import com.berlin.domain.usecase.task.DeleteTaskUseCase
import com.berlin.domain.usecase.task.GetAllTasksUseCase
import com.berlin.presentation.PermissionedUiRunner
import com.berlin.presentation.helper.choose
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer

class DeleteTaskUI(
    private val deleteTask: DeleteTaskUseCase,
    private val getAllTasks: GetAllTasksUseCase,
    private val viewer: Viewer,
    private val reader: Reader,
) : PermissionedUiRunner {

    override val id: Int = 3
    override val label: String = "Delete task"

    override fun isAllowed(permission: Permission) = permission.deleteTask

    override fun run() {
        try {
            val task = choose(
                title = "Tasks",
                elements = getAllTasks(),
                labelOf = { "${it.id} – ${it.title}" },
                viewer = viewer,
                reader = reader
            )

            viewer.show("Type Y to confirm deletion:")
            if (!reader.read().equals("y", true)) throw InputCancelledException("")

            deleteTask(task.id).onSuccess {
                    viewer.show("Deleted.")
                }.onFailure { viewer.show(it.message ?: "Deletion failed") }

        } catch (ex: InputCancelledException) {
            viewer.show("Cancelled.")
        } catch (ex: InvalidSelectionException) {
            viewer.show("Invalid selection")
        } catch (ex: InvalidTaskIdException) {
            viewer.show("invalid task id")
        }
    }
}
