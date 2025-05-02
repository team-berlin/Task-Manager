package com.berlin.presentation.project

import com.berlin.logic.usecase.project.GetProjectByIdUseCase
import com.berlin.model.Project
import com.berlin.presentation.UiRunner
import com.berlin.presentation.input_output.Reader
import com.berlin.presentation.input_output.Viewer

class GetProjectByIdUi(
    private val getProjectByIdUseCase: GetProjectByIdUseCase,
    private val viewer: Viewer,
    private val reader: Reader
): UiRunner {
    override val id: Int = 4
    override val label: String = "View Project Details"

    override fun run() {
        val projectId = getProjectId()

        try {
            val project = getProjectByIdUseCase.getProjectById(projectId)
            displayProjectDetails(project)
        } catch (e: Exception) {
            viewer.display("Error retrieving project: ${e.message}\n")
        }
    }

    private fun getProjectId(): String {
        viewer.display("Enter project id to view its details:\n")

        while (true) {
            reader.getUserInput()?.let { input ->
                if (input.isNotBlank()) {
                    return input
                }
            }
            viewer.display("Please enter a valid project id:")
        }
    }

    private fun displayProjectDetails(project: Project) {
        displayProjectHeader(project)
        displayProjectStatesAndTasks(project)
    }

    private fun displayProjectHeader(project: Project) {
        viewer.display("=== Project Title: ${project.name} ===\n")
        viewer.display("=== Project Description: ${project.description ?: "No description"} ===\n")
        viewer.display("================================================================\n")
    }

    // TODO("States and Tasks information below must be handled after merging")

    private fun displayProjectStatesAndTasks(project: Project) {
        project.statesId?.takeIf { it.isNotEmpty() }?.forEach { state ->
            displayState(state)
            displayTasksForState(project, state)
        } ?: viewer.display("No states defined for this project.\n")
    }

    private fun displayState(state: String) {
        viewer.display("State: [${state}] ${state}\n")
        viewer.display("----------------------------------------------------------------\n")
    }

    private fun displayTasksForState(project: Project, state: String) {
        project.tasksId?.takeIf { it.isNotEmpty() }?.forEach { task ->
            viewer.display("  - Task ID: ${task}, Title: ${task}, Assigned to: ${task}\n\n")
        } ?: viewer.display("  No tasks for this state.\n\n")
    }
}