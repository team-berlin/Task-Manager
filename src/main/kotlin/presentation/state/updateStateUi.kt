package com.berlin.presentation.state

import com.berlin.domain.exception.InvalidStateNameException
import com.berlin.domain.usecase.state.GetAllStatesUseCase
import com.berlin.domain.usecase.state.UpdateStateUseCase
import com.berlin.presentation.UiRunner
import com.berlin.presentation.helper.choose
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer

class UpdateStateUi(
    private val updateState: UpdateStateUseCase,
    private val getAllStates: GetAllStatesUseCase,
    private val viewer: Viewer,
    private val reader: Reader,
) : UiRunner {
    override val id: Int = 50
    override val label: String = "Update State"
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
            updateState.updateState(
                state.id,
                newName,
                state.projectId
            )
                .onSuccess { viewer.show("Updated Successfully") }
                .onFailure { viewer.show("Update Failed") }

        } catch (ex: InvalidStateNameException) {
            viewer.show("State Name must not be empty or blank")
        }

    }
}