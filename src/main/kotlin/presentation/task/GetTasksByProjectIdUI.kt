package com.berlin.presentation.task

import com.berlin.domain.exception.InputCancelledException
import com.berlin.domain.exception.InvalidSelectionException
import com.berlin.domain.model.Task
import com.berlin.presentation.helper.choose
import org.berlin.data.DummyData
import org.berlin.presentation.UiRunner
import org.berlin.presentation.input_output.Reader
import org.berlin.presentation.input_output.Viewer

class GetTasksByProjectIdUI(
    private val viewer: Viewer,
    private val reader: Reader,
) : UiRunner {

    override val id: Int = 4
    override val label: String = "View tasks by project"

    override fun run() {
        try {
            val project = chooseProject()
            showSwimLaneFor(project.id)
        } catch (ex: InputCancelledException) {
            viewer.show("Cancelled.")
        } catch (ex: InvalidSelectionException) {
            viewer.show("{ex.message}")
        }
    }

    private fun chooseProject() = choose(
        title = "Projects",
        elements = DummyData.projects,
        labelOf = { it.name },
        viewer = viewer,
        reader = reader
    )

    private fun showSwimLaneFor(projectId: String) {
        val states = DummyData.states.filter { it.projectId == projectId }
        if (states.isEmpty()) {
            viewer.show("No states found for that project.")
            return
        }

        val tasks = DummyData.tasks.filter { it.projectId == projectId }

        viewer.show("\n=== Tasks for project $projectId ===")
        states.forEach { state ->
            viewer.show("\n[${state.name}]")
            tasksInState(tasks, state.id).forEach { line -> viewer.show(line) }
        }
    }

    private fun tasksInState(all: List<Task>, stateId: String): List<String> {
        val tasksHere = all.filter { it.stateId == stateId }
        if (tasksHere.isEmpty()) return listOf("  (no tasks)")
        return tasksHere.map { "- ${it.id}: ${it.title}  → ${it.assignedToUserId}" }
    }
}
