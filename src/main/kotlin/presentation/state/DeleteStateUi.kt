package com.berlin.presentation.state

import com.berlin.domain.exception.InputCancelledException
import com.berlin.domain.exception.InvalidSelectionException
import com.berlin.domain.exception.InvalidStateIdException
import com.berlin.domain.model.Permission
import com.berlin.domain.usecase.state.DeleteStateUseCase
import com.berlin.domain.usecase.state.GetAllStatesUseCase
import com.berlin.presentation.PermissionedUiRunner
import com.berlin.presentation.helper.choose
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer

class DeleteStateUi(
    private val deleteStateUseCase: DeleteStateUseCase,
    private val getAllStates: GetAllStatesUseCase,
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

            deleteStateUseCase.deleteState(state.id)
                .onSuccess {
                    viewer.show("Deleted.")
                }.onFailure {
                    viewer.show(it.message ?: "Deletion failed")
                }
        } catch (ex: InputCancelledException) {
            viewer.show("Cancelled.")
        } catch (ex: InvalidSelectionException) {
            viewer.show("Invalid selection")
        } catch (ex: InvalidStateIdException) {
            viewer.show("invalid state id")
        }

    }
}