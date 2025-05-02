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
        displayHeader()
        val projects = displayAvailableProjects()

        if (projects.isEmpty()) {
            viewer.display("No projects available to delete.\n")
            return
        }

        val projectId = getValidProjectId(projects.map { it.id })
        deleteProject(projectId)
    }

    private fun displayHeader() {
        viewer.display("=== Delete Project ===\n")
        viewer.display("================================================================\n\n")
    }

    private fun displayAvailableProjects() = getAllProjectsUseCase.getAllProjects().also { projects ->
        viewer.display("Available Projects:\n")
        projects.forEach { project ->
            viewer.display("Project ID: ${project.id}, Title: ${project.name}")
        }
    }

    private fun getValidProjectId(validIds: List<String>): String {
        viewer.display("Enter project id to delete:\n")

        while (true) {
            reader.getUserInput()?.let { inputId ->
                if (validIds.contains(inputId)) {
                    return inputId
                }
            }

            viewer.display("Please enter a valid project id from the list above:")
        }
    }

    private fun deleteProject(projectId: String) {
        viewer.display("Deleting project...\n")

        val deletionResult = deleteProjectUseCase.deleteProject(projectId)

        if (deletionResult.isSuccess) {
            viewer.display("Project deleted successfully!\n")
        } else {
            viewer.display("Project deletion failed!\n")
        }
    }
}