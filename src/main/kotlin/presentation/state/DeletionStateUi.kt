package com.berlin.presentation.state

import com.berlin.data.DummyData
import com.berlin.domain.exception.InputCancelledException
import com.berlin.domain.exception.InvalidSelectionException
import com.berlin.domain.exception.InvalidStateIdException
import com.berlin.domain.usecase.state.DeletionStateUseCase
import com.berlin.domain.usecase.state.GetAllStatesByProjectIdUseCase
import com.berlin.domain.usecase.state.GetAllStatesUseCase
import com.berlin.presentation.UiRunner
import com.berlin.presentation.helper.choose
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer

class DeletionStateUi(
    private val deletionStateUseCase: DeletionStateUseCase,
    private val getAllStates: GetAllStatesUseCase,
    private val viewer: Viewer,
    private val reader: Reader
) : UiRunner {
    override val id: Int = 2
    override val label: String = "Delete State"

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
            if (!reader.read().equals("y", true)) throw InputCancelledException("")

            deletionStateUseCase.deleteState(state.id)
                .onSuccess {
                    DummyData.state.remove(state)
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
