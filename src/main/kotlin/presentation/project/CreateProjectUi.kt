package com.berlin.presentation.project

import com.berlin.domain.usecase.project.CreateProjectUseCase
import com.berlin.presentation.UiRunner
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer

class CreateProjectUi(
    private val createProjectUseCase: CreateProjectUseCase,
    private val viewer: Viewer,
    private val reader: Reader
) : UiRunner {
    override val id: Int = 1
    override val label: String = "Create New Project"

    override fun run() {

        displayHeader()

        val projectName = getProjectName()

        val projectDescription = getProjectDescription()

        createProject(projectName, projectDescription)
    }

    private fun displayHeader() {
        viewer.show("=== Create New Project ===\n")
        viewer.show("================================================================\n\n")
        viewer.show("Enter project details:\n")
    }

    private fun getProjectName(): String {
        viewer.show("Project Title:")

        while (true) {
            reader.read()?.let { input ->
                if (input.isNotEmpty()) {
                    return input
                }
            }
            viewer.show("Please enter a valid project name:")
        }
    }

    private fun getProjectDescription(): String? {
        viewer.show("Do you want to write a description? (yes/no)")
        val option = reader.read()?.lowercase()
        return if (option == "yes") {
            viewer.show("Enter project description:")
            reader.read()
        } else {
            null
        }
    }

    private fun createProject(projectName: String, projectDescription: String?) {
        viewer.show("Creating project...\n")

        val creationResult = createProjectUseCase.createNewProject(
            projectName,
            projectDescription,
            null,
            null
        )

        creationResult.fold(
            onSuccess = { message ->
                viewer.show(message)
            },
            onFailure = { error ->
                viewer.show("Error: ${error.message ?: "Project creation failed!"}\n")
            }
        )
    }
}