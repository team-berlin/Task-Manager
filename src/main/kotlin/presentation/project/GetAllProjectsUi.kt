package com.berlin.presentation.project

import com.berlin.logic.usecase.project.GetAllProjectsUseCase
import com.berlin.presentation.UiRunner
import com.berlin.presentation.input_output.Viewer

class GetAllProjectsUi(
    private val getAllProjectsUseCase: GetAllProjectsUseCase,
    private val viewer: Viewer,
) : UiRunner {
    override val id: Int = 2
    override val label: String = "Get All Projects"

    override fun run() {
        displayHeader()
        displayProjects()
    }

    private fun displayHeader() {
        viewer.display("=== Available Projects ===\n")
        viewer.display("================================================================\n\n")
    }

    private fun displayProjects() {
        getAllProjectsUseCase.getAllProjects().let { projects ->
            when {
                projects.isNotEmpty() -> {
                    projects.forEach { project ->
                        viewer.display("Project ID: ${project.id}, Title: ${project.name}")
                    }
                }
                else -> {
                    viewer.display("No projects available.\n")
                }
            }
        }
    }
}