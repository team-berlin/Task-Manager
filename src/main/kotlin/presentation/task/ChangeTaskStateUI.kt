package com.berlin.presentation.task

import com.berlin.data.DummyData
import com.berlin.domain.exception.InputCancelledException
import com.berlin.domain.exception.InvalidSelectionException
import com.berlin.domain.exception.InvalidTaskStateException
import com.berlin.domain.exception.TaskNotFoundException
import com.berlin.domain.model.State
import com.berlin.domain.usecase.task.ChangeTaskStateUseCase
import com.berlin.domain.usecase.task.GetAllTasksUseCase
import com.berlin.presentation.UiRunner
import com.berlin.presentation.helper.choose
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer

class ChangeTaskStateUI(
    private val changeState: ChangeTaskStateUseCase,
    private val getAllTasks: GetAllTasksUseCase,
    private val viewer: Viewer,
    private val reader: Reader
) : UiRunner {

    override val id: Int = 6
    override val label: String = "Change task state"

    override suspend fun run() {
        try {
            val task = choose(
                title    = "Tasks",
                elements = getAllTasks(),
                labelOf  = { "${it.id} – ${it.title} [${it.stateId}]" },
                viewer   = viewer,
                reader   = reader
            )

            val possible = DummyData.states.filter { it.projectId == task.projectId }
            if (possible.isEmpty()) {
                viewer.show("No states defined for project ${task.projectId}")
                return
            }
            val state = choose(
                title    = "States for project ${task.projectId}",
                elements = possible,
                labelOf  = { (it as State).name },
                viewer   = viewer,
                reader   = reader
            )

            changeState(task.id, state.id)
                .onSuccess { viewer.show("Task ${task.id} moved to ${state.name}") }
                .onFailure { viewer.show(it.message ?: "Failed to change state") }

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
