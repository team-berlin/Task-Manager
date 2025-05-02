package com.berlin.presentation.project

import com.berlin.logic.usecase.project.GetAllProjectsUseCase
import com.berlin.logic.usecase.project.GetProjectByIdUseCase
import com.berlin.logic.usecase.project.UpdateProjectUseCase
import com.berlin.model.Project
import com.berlin.presentation.UiRunner
import com.berlin.presentation.input_output.Reader
import com.berlin.presentation.input_output.Viewer

class UpdateProjectUi(
    private val updateProjectUseCase: UpdateProjectUseCase,
    private val getAllProjectsUseCase: GetAllProjectsUseCase,
    private val getProjectByIdUseCase: GetProjectByIdUseCase,
    private val viewer: Viewer,
    private val reader: Reader
) : UiRunner {
    override val id: Int = 5
    override val label: String = "Update Project"

    override fun run() {
        displayHeader()
        displayAvailableProjects()

        val projectId = getValidProjectId()
        var project = getProjectByIdUseCase.getProjectById(projectId)

        displayCurrentProjectDetails(project)

        project = updateProjectFields(project)

        submitProjectUpdate(project)
    }

    private fun displayHeader() {
        viewer.display("=== Update Project ===\n")
        viewer.display("================================================================\n")
    }

    private fun displayAvailableProjects() {
        viewer.display("Available Projects:\n")
        getAllProjectsUseCase.getAllProjects().forEach { project ->
            viewer.display("Project ID: ${project.id}, Title: ${project.name}")
        }
    }

    private fun getValidProjectId(): String {
        viewer.display("Enter project id to update:\n")

        while (true) {
            val projectId = reader.getUserInput()

            if (projectId.isNullOrBlank()) {
                viewer.display("Project id cannot be empty. Please enter a valid project id:\n")
                continue
            }

            if (isProjectIdValid(projectId)) {
                return projectId
            } else {
                viewer.display("Project id not found. Please enter a valid project id:\n")
            }
        }
    }

    private fun isProjectIdValid(projectId: String): Boolean {
        return getAllProjectsUseCase.getAllProjects().any { it.id == projectId }
    }

    private fun displayCurrentProjectDetails(project: Project) {
        viewer.display("Current Project Details:\n")
        viewer.display("Project ID: ${project.id}\n")
        viewer.display("Title: ${project.name}\n")
        viewer.display("Description: ${project.description ?: "No description"}\n")
    }

    private fun updateProjectFields(initialProject: Project): Project {
        var currentProject = initialProject

        do {
            displayUpdateOptions()
            val choice = promptForUpdateChoice()

            currentProject = when (choice) {
                1 -> updateProjectTitle(currentProject)
                2 -> updateProjectDescription(currentProject)
                else -> throw IllegalArgumentException("Invalid option: $choice")
            }
        } while (shouldContinueUpdating())

        return currentProject
    }

    private fun displayUpdateOptions() {
        viewer.display("Select field to update:\n")
        viewer.display("1. Title\n")
        viewer.display("2. Description\n")
    }

    private fun promptForUpdateChoice(): Int {
        viewer.display("Enter choice (1 or 2): ")

        while (true) {
            val input = reader.getUserInput()

            if (input.isNullOrBlank()) {
                viewer.display("Invalid choice. Please enter 1 or 2: ")
                continue
            }

            return try {
                val choice = input.toInt()
                if (choice in 1..2) {
                    choice
                } else {
                    viewer.display("Please enter 1 or 2: ")
                    continue
                }
            } catch (_: NumberFormatException) {
                viewer.display("Please enter a number (1 or 2): ")
                continue
            }
        }
    }

    private fun updateProjectTitle(project: Project): Project {
        viewer.display("Enter new title: ")

        while (true) {
            val title = reader.getUserInput()

            if (title.isNullOrBlank()) {
                viewer.display("Project title cannot be empty. Please enter a valid title: ")
                continue
            }

            return project.copy(name = title)
        }
    }

    private fun updateProjectDescription(project: Project): Project {
        viewer.display("Enter new description: ")
        val description = reader.getUserInput()

        return project.copy(description = description)
    }

    private fun shouldContinueUpdating(): Boolean {
        viewer.display("Would you like to update another field? (yes/no): ")

        while (true) {
            val option = reader.getUserInput()?.lowercase()

            return when (option) {
                "yes" -> true
                "no" -> false
                else -> {
                    viewer.display("Please enter 'yes' or 'no': ")
                    continue
                }
            }
        }
    }

    private fun submitProjectUpdate(project: Project) {
        viewer.display("Updating project...\n")

        val result = updateProjectUseCase.updateProject(project)

        if (result.isSuccess) {
            viewer.display("Project updated successfully!\n")
        } else {
            viewer.display("Project update failed!\n")
        }
    }
}