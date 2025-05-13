package com.berlin.presentation.state

import com.berlin.domain.exception.InputCancelledException
import com.berlin.domain.exception.InvalidSelectionException
import com.berlin.domain.exception.InvalidStateNameException
import com.berlin.domain.model.Permission
import com.berlin.domain.usecase.state.GetAllStatesUseCase
import com.berlin.domain.usecase.state.UpdateStateUseCase
import com.berlin.presentation.PermissionedUiRunner
import com.berlin.presentation.helper.choose
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer

class UpdateStateUI(
    private val updateState: UpdateStateUseCase,
    private val getAllStates: GetAllStatesUseCase,
    private val viewer: Viewer,
    private val reader: Reader,
) : PermissionedUiRunner {

    override val id: Int = 5
    override val label: String = "Update State"

    override fun isAllowed(permission: Permission) = permission.updateState

    override fun run() {
        try {

            val state = choose(
                title = "States to update",
                elements = getAllStates(),
                labelOf = { "${it.id} – ${it.name}" },
                viewer = viewer,
                reader = reader
            )

            viewer.show("Enter new state name ((or X to keep ${state.name}): ")
            val newName = reader.read()?.trim().orEmpty()
            updateState(
                state.id,
                newName,
                state.projectId
            )
            viewer.show(" $newName is updated Successfully")

        } catch (ex: InvalidStateNameException) {
            viewer.show("State Name must not be empty or blank")
        }  catch (_: InputCancelledException) {
        viewer.show("Cancelled!")
    }catch (_: InvalidSelectionException){
            viewer.show("invalid selection")
    }

    }
}