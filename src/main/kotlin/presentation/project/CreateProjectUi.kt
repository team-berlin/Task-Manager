package com.berlin.presentation.project

import com.berlin.logic.usecase.project.CreateProjectUseCase
import com.berlin.presentation.UiRunner
import com.berlin.presentation.input_output.Reader
import com.berlin.presentation.input_output.Viewer

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
        viewer.display("=== Create New Project ===\n")
        viewer.display("================================================================\n\n")
        viewer.display("Enter project details:\n")
    }

    private fun getProjectName(): String {
        viewer.display("Project Title:")

        while (true) {
            reader.getUserInput()?.let { input ->
                if (input.isNotBlank()) {
                    return input
                }
            }
            viewer.display("Please enter a valid project name:")
        }
    }

    private fun getProjectDescription(): String? {
        viewer.display("Do you want to write a description? (yes/no)")
        val option = reader.getUserInput()?.lowercase()

        return if (option == "yes") {
            viewer.display("Enter project description:")
            reader.getUserInput()
        } else {
            null
        }
    }

    private fun createProject(projectName: String, projectDescription: String?) {
        viewer.display("Creating project...\n")

        val creationResult = createProjectUseCase.createNewProject(
            projectName,
            projectDescription,
            null,
            null
        )

        if (creationResult.isSuccess) {
            viewer.display("Project created successfully!\n")
        } else {
            viewer.display("Project creation failed!\n")
            }
        }
    }