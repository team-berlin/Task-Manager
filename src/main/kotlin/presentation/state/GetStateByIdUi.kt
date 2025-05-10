package com.berlin.presentation.state

import com.berlin.domain.exception.StateNotFoundException
import com.berlin.domain.model.Permission
import com.berlin.domain.model.TaskState
import com.berlin.domain.usecase.state.GetStateByIdUseCase
import com.berlin.presentation.PermissionedUiRunner
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer

class GetStateByIdUi(
    private val getStateById: GetStateByIdUseCase,
    private val viewer: Viewer,
    private val reader: Reader,
) : PermissionedUiRunner {

    override val id: Int = 4
    override val label: String = "Get state by ID"

    override fun isAllowed(permission: Permission) = permission.getStateById

    override fun run() {
        viewer.show("Enter state ID: ")
        val stateId = reader.read()?.trim().orEmpty()
        getStateById.getStateById(stateId)
            .onSuccess { showState(it) }
            .onFailure { ex ->
            when (ex) {
                is StateNotFoundException ->
                    viewer.show("No state found with ID “$stateId”")

                else ->
                    viewer.show(ex.message ?: "Lookup failed")
            }
        }

    }

    private fun showState(state: TaskState) {
        viewer.show("ID: ${state.id}")
        viewer.show("Title: ${state.name}")
        viewer.show("Project ID: ${state.projectId}")
    }
}