package com.berlin.presentation.project

import com.berlin.logic.usecase.project.DeleteProjectUseCase
import com.berlin.logic.usecase.project.GetAllProjectsUseCase
import com.berlin.presentation.UiRunner
import com.berlin.presentation.input_output.Reader
import com.berlin.presentation.input_output.Viewer

class DeleteProjectUi(
    private val deleteProjectUseCase: DeleteProjectUseCase,
    private val getAllProjectsUseCase: GetAllProjectsUseCase,
    private val viewer: Viewer,
    private val reader: Reader
) : UiRunner {
    override val id: Int = 3
    override val label: String = "Delete Project"

    override fun run() {
        viewer.display("=== Delete Project ===\n")
        viewer.display("================================================================\n\n")
        viewer.display("Available Projects:\n")
        getAllProjectsUseCase.getAllProjects().forEach { project ->
            viewer.display("Project ID: ${project.id}, Title: ${project.name}")
        }
        viewer.display("Enter project id to delete:\n")
        var projectId: String?
        var isValid = false
        while (true) {
            projectId = reader.getUserInput()
            if (projectId == null) {
                throw Exception("Project id can not be null")
            } else {
                for (project in getAllProjectsUseCase.getAllProjects()) {
                    if (projectId == project.id) {
                        isValid = true
                        break
                    }
                }
            }
            if (!isValid) {
                throw Exception("Please enter a valid project id")
            } else {
                break
            }
        }
        viewer.display("deleting project...\n")
        val deletionProcess = deleteProjectUseCase.deleteProject(projectId)
        if (deletionProcess.isSuccess) {
            viewer.display("Project deleted Successfully!\n")
        } else {
            viewer.display("Project deletion failed!\n")
        }
    }

}