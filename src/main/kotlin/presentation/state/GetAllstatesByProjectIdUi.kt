package com.berlin.presentation.state

import com.berlin.data.DummyData
import com.berlin.domain.exception.InputCancelledException
import com.berlin.domain.exception.InvalidProjectIdException
import com.berlin.domain.exception.InvalidSelectionException
import com.berlin.domain.model.State
import com.berlin.domain.usecase.project.GetAllProjectsUseCase
import com.berlin.domain.usecase.state.GetAllStatesByProjectIdUseCase
import com.berlin.presentation.UiRunner
import com.berlin.presentation.helper.choose
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer

class GetAllStatesByProjectIdUi(
    private val getAllStatesByProjectIdUseCase: GetAllStatesByProjectIdUseCase,
    private val getAllProjects: GetAllProjectsUseCase,
    private val viewer: Viewer,
    private val reader: Reader,
) : UiRunner {

    override val id: Int = 3000
    override val label: String = "Get current states for a specific project"

    override fun run() {
        try {
            val project = choose(
                title = "Projects",
                elements = getAllProjects.getAllProjects(),
                labelOf = { it.name },
                viewer = viewer,
                reader = reader
            )

            getAllStatesByProjectIdUseCase.getAllStatesByProjectId(project.id)
                .onSuccess { state -> showSwimLaneFor(project.id, state) }
                .onFailure { viewer.show(it.message ?: "Failed to load states") }


        } catch (ex: InputCancelledException) {
            viewer.show("Cancelled.")
        } catch (ex: InvalidSelectionException) {
            viewer.show("Invalid selection")
        } catch (ex: InvalidProjectIdException) {
            viewer.show("invalid project id")
        }
    }

    private fun showSwimLaneFor(projectId: String, states: List<State>) {
        val projects = DummyData.projects.filter { it.id == projectId }
        if (projects.isEmpty()) {
            viewer.show("No projects found")
            return
        }
        viewer.show("\n=== States for project $projectId ===")
        projects.forEach { project ->
            viewer.show("\n[${project.name}]")
            stateInProject(states, project.id).forEach { line ->
                viewer.show(line)
            }
        }
    }

    private fun stateInProject(all: List<State>, projectId: String): List<String> {
        val here = all.filter { it.projectId == projectId }
        if (here.isEmpty()) return listOf("  (no states)")
        return here.map { "- ${it.id}: ${it.name}" }
    }
}