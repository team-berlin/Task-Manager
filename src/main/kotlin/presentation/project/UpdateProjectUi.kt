package com.berlin.presentation.project

import com.berlin.domain.model.Project
import com.berlin.domain.usecase.project.GetAllProjectsUseCase
import com.berlin.domain.usecase.project.GetProjectByIdUseCase
import com.berlin.domain.usecase.project.UpdateProjectUseCase
import com.berlin.presentation.UiRunner
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer

class UpdateProjectUi(
    private val updateProjectUseCase: UpdateProjectUseCase,
    private val getAllProjectsUseCase: GetAllProjectsUseCase,
    private val getProjectByIdUseCase: GetProjectByIdUseCase,
    private val viewer: Viewer,
    private val reader: Reader
) : UiRunner {
    override val id: Int = 15
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
        viewer.show("=== Update Project ===\n")
        viewer.show("================================================================\n")
    }

    private fun displayAvailableProjects() {
        viewer.show("Available Projects:\n")
        getAllProjectsUseCase.getAllProjects().forEach { project ->
            viewer.show("Project ID: ${project.id}, Title: ${project.name}")
        }
    }

    private fun getValidProjectId(): String {
        viewer.show("Enter project id to update:\n")

        while (true) {
            val projectId = reader.read()

            if (projectId.isNullOrBlank()) {
                viewer.show("Project id cannot be empty. Please enter a valid project id:\n")
                continue
            }

            if (isProjectIdValid(projectId)) {
                return projectId
            } else {
                viewer.show("Project id not found. Please enter a valid project id:\n")
            }
        }
    }

    private fun isProjectIdValid(projectId: String): Boolean {
        return try {
            getProjectByIdUseCase.getProjectById(projectId)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun displayCurrentProjectDetails(project: Project) {
        viewer.show("Current Project Details:\n")
        viewer.show("Project ID: ${project.id}\n")
        viewer.show("Title: ${project.name}\n")
        viewer.show("Description: ${project.description ?: "No description"}\n")
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
        viewer.show("Select field to update:\n")
        viewer.show("1. Title\n")
        viewer.show("2. Description\n")
    }

    private fun promptForUpdateChoice(): Int {
        viewer.show("Enter choice (1 or 2): ")

        while (true) {
            val input = reader.read()

            if (input.isNullOrBlank()) {
                viewer.show("Invalid choice. Please enter 1 or 2: ")
                continue
            }

            return try {
                val choice = input.toInt()
                if (choice in 1..2) {
                    choice
                } else {
                    viewer.show("Please enter 1 or 2: ")
                    continue
                }
            } catch (_: NumberFormatException) {
                viewer.show("Please enter a number (1 or 2): ")
                continue
            }
        }
    }

    private fun updateProjectTitle(project: Project): Project {
        viewer.show("Enter new title: ")

        while (true) {
            val title = reader.read()

            if (title.isNullOrBlank()) {
                viewer.show("Project title cannot be empty. Please enter a valid title: ")
                continue
            }

            return project.copy(name = title)
        }
    }

    private fun updateProjectDescription(project: Project): Project {
        viewer.show("Enter new description: ")
        val description = reader.read()

        return project.copy(description = description)
    }

    private fun shouldContinueUpdating(): Boolean {
        viewer.show("Would you like to update another field? (yes/no): ")

        while (true) {
            val option = reader.read()?.lowercase()

            return when (option) {
                "yes" -> true
                "no" -> false
                else -> {
                    viewer.show("Please enter 'yes' or 'no': ")
                    continue
                }
            }
        }
    }

    private fun submitProjectUpdate(project: Project) {
        viewer.show("Updating project...\n")

        val result = updateProjectUseCase.updateProject(project)

        if (result.isSuccess) {
            viewer.show("Project updated successfully!\n")
        } else {
            viewer.show("Project update failed!\n")
        }
    }
}