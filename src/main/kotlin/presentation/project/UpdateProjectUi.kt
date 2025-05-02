package com.berlin.presentation.project

import com.berlin.logic.usecase.project.GetAllProjectsUseCase
import com.berlin.logic.usecase.project.GetProjectByIdUseCase
import com.berlin.logic.usecase.project.UpdateProjectUseCase
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

        viewer.display("=== Update Project ===\n")
        viewer.display("================================================================\n")
        viewer.display("Available Projects:\n")
        getAllProjectsUseCase.getAllProjects().forEach { project ->
            viewer.display("Project ID: ${project.id}, Title: ${project.name}")
        }
        viewer.display("Enter project id to update:\n")
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

        val project = getProjectByIdUseCase.getProjectById(projectId)
        viewer.display("Current Project Details:\n")
        viewer.display("Project ID: ${project.id}\n")
        viewer.display("Title: ${project.name}\n")
        viewer.display("Description: ${project.description}\n")

        while (true) {
            viewer.display("Select field to update:\n")
            viewer.display("1. Title\n")
            viewer.display("2. Description\n")
            viewer.display("Enter choice (1 or 2): ")
            val input = reader.getUserInput()?.toInt()
            if (input == 1) {
                viewer.display("Enter new title: ")
                var projectName: String?
                while (true) {
                    projectName = reader.getUserInput()
                    if (projectName == null) {
                        throw Exception("Please enter a valid project name")
                    } else {
                        project.name = projectName
                        break
                    }
                }
            } else if (input == 2) {
                viewer.display("Enter new description: ")
                val description = reader.getUserInput()
                project.description = description
            } else {
                throw Exception("Enter a valid input")
            }
            viewer.display("Updating project...\n")

            viewer.display("Would you like to update another field? (yes/no): ")
            val option = reader.getUserInput()
            if (option?.lowercase() == "no") {
                break
            } else if (option?.lowercase() == "yes") {
                continue
            } else {
                throw Exception("Please enter a valid option")
            }
        }
        val updatedProject = updateProjectUseCase.updateProject(project)
        if (updatedProject.isSuccess) {
            viewer.display("Project updated successfully!\n")
        } else {
            viewer.display("Project update failed!\n")
        }
    }
}