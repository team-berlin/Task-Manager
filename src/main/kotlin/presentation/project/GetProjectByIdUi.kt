package com.berlin.presentation.project

import com.berlin.logic.usecase.project.GetProjectByIdUseCase
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
        viewer.display("Enter project id to view its details:\n")
        var projectId: String?
        while (true) {
            projectId = reader.getUserInput()
            if (projectId == null) {
                throw Exception("Project id can not be null")
            } else {
                break
            }
        }
        val currentProject = getProjectByIdUseCase.getProjectById(projectId)
        viewer.display("=== Project Title: ${currentProject.name} ===\n")
        viewer.display("=== Project Description: ${currentProject.description} ===\n")
        viewer.display("================================================================\n")
        currentProject.statesId?.forEach{ state ->
            viewer.display("State: [${TODO("state id")}] ${TODO("state name")}\n")
            viewer.display("----------------------------------------------------------------\n")
            currentProject.tasksId?.forEach { task ->
                viewer.display("  - Task ID: ${TODO("task id")}, Title: ${TODO("task name")}, Assigned to: ${TODO("mate name")}\n\n")
            }
        }
    }
}