package com.berlin.presentation.task_state

import com.berlin.domain.exception.InvalidStateIdException
import com.berlin.domain.model.Permission
import com.berlin.domain.model.TaskState
import com.berlin.domain.usecase.task_state.GetTaskStateByIdUseCase
import com.berlin.presentation.PermissionedUiRunner
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer

class GetTaskStateByIdUi(
    private val getStateById: GetTaskStateByIdUseCase,
    private val viewer: Viewer,
    private val reader: Reader,
) : PermissionedUiRunner {

    override val id: Int = 4
    override val label: String = "Get state by ID"

    override fun isAllowed(permission: Permission) = permission.getStateById

    override fun run() {
        viewer.show("Enter state ID: ")
        try {
            val stateId = reader.read()?.trim().orEmpty()
            val state = getStateById(stateId)
            showState(state)
        } catch (_: InvalidStateIdException) {
            viewer.show("invalid id")
        }

    }

    private fun showState(state: TaskState) {
        viewer.show("ID: ${state.id}")
        viewer.show("Title: ${state.name}")
        viewer.show("Project ID: ${state.projectId}")
    }
}