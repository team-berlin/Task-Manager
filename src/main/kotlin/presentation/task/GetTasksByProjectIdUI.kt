// src/main/kotlin/com/berlin/presentation/task/GetTasksByProjectIdUI.kt
package com.berlin.presentation.task

import com.berlin.data.DummyData
import com.berlin.domain.exception.InputCancelledException
import com.berlin.domain.exception.InvalidProjectIdException
import com.berlin.domain.exception.InvalidSelectionException
import com.berlin.domain.model.Task
import com.berlin.domain.usecase.task.GetTasksByProjectUseCase
import com.berlin.presentation.UiRunner
import com.berlin.presentation.helper.choose
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer

class GetTasksByProjectIdUI(
    private val getTasks: GetTasksByProjectUseCase,
    private val viewer: Viewer,
    private val reader: Reader,
) : UiRunner {

    override val id: Int = 4
    override val label: String = "View tasks by project"

    override suspend fun run() {
        try {
            val project = choose(
                title = "Projects",
                elements = DummyData.projects,
                labelOf = { it.name },
                viewer = viewer,
                reader = reader
            )

            getTasks(project.id).onSuccess { tasks -> showSwimLaneFor(project.id, tasks) }
                .onFailure { viewer.show(it.message ?: "Failed to load tasks") }

        } catch (ex: InputCancelledException) {
            viewer.show("Cancelled.")
        } catch (ex: InvalidSelectionException) {
            viewer.show("Invalid selection")
        } catch (ex: InvalidProjectIdException) {
            viewer.show("invalid project id")
        }
    }

    private fun showSwimLaneFor(projectId: String, tasks: List<Task>) {
        val states = DummyData.states.filter { it.projectId == projectId }
        if (states.isEmpty()) {
            viewer.show("No states found for that project.")
            return
        }

        viewer.show("\n=== Tasks for project $projectId ===")
        states.forEach { state ->
            viewer.show("\n[${state.name}]")
            tasksInState(tasks, state.id).forEach { line ->
                viewer.show(line)
            }
        }
    }

    private fun tasksInState(all: List<Task>, stateId: String): List<String> {
        val here = all.filter { it.stateId == stateId }
        if (here.isEmpty()) return listOf("  (no tasks)")
        return here.map { "- ${it.id}: ${it.title}  → ${it.assignedToUserId}" }
    }
}
