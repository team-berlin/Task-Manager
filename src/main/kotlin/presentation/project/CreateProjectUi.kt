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
        viewer.display("=== Create New Project ===\n")
        viewer.display("================================================================\n\n")
        viewer.display("Enter project details:\n")
        viewer.display("Project Title:")
        var projectName: String?
        while (true) {
            projectName = reader.getUserInput()
            if (projectName == null) {
                throw Exception("Please enter a valid project name")
            } else {
                break
            }
        }
        viewer.display("Do you want to write a description? (yes/no)")
        val option = reader.getUserInput()
        var projectDescription: String? = null
        if (option?.lowercase() == "yes") {
            projectDescription = reader.getUserInput()
        }
        viewer.display("Creating project...\n")

        val creationProcess = createProjectUseCase.createNewProject(projectName, projectDescription, null, null)

        if (creationProcess.isSuccess) {
            viewer.display("Project created successfully!\n")
        } else {
            viewer.display("Project creation failed!\n")
        }
    }


}