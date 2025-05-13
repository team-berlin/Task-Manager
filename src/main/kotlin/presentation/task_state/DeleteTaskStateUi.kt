package com.berlin.presentation.task_state

import com.berlin.domain.exception.InputCancelledException
import com.berlin.domain.exception.InvalidSelectionException
import com.berlin.domain.exception.InvalidStateIdException
import com.berlin.domain.model.Permission
import com.berlin.domain.usecase.task_state.DeleteTaskStateUseCase
import com.berlin.domain.usecase.task_state.GetAllTaskStatesUseCase
import com.berlin.presentation.PermissionedUiRunner
import com.berlin.presentation.helper.choose
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer

class DeleteTaskStateUi(
    private val deleteTaskStateUseCase: DeleteTaskStateUseCase,
    private val getAllStates: GetAllTaskStatesUseCase,
    private val viewer: Viewer,
    private val reader: Reader
) : PermissionedUiRunner {

    override val id: Int = 2
    override val label: String = "Delete State"

    override fun isAllowed(permission: Permission) = permission.deleteState

    override fun run() {

        try {

            val state = choose(
                title = "States",
                elements = getAllStates(),
                labelOf = { "${it.id} – ${it.name}" },
                viewer = viewer,
                reader = reader
            )
            viewer.show("Type Y to confirm deletion:")
            if (!reader.read().equals("y", true)) throw InputCancelledException("Cancelled.")

            val deleteStateResult = deleteTaskStateUseCase(state.id)
            viewer.show("$deleteStateResult is Deleted.")

        } catch (ex: InputCancelledException) {
            viewer.show("Cancelled.")
        } catch (ex: InvalidSelectionException) {
            viewer.show("Invalid selection")
        } catch (ex: InvalidStateIdException) {
            viewer.show("invalid state id")
        }

    }
}