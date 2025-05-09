package com.berlin.presentation.project

import com.berlin.domain.model.Permission
import com.berlin.domain.usecase.project.GetAllProjectsUseCase
import com.berlin.presentation.PermissionedUiRunner
import com.berlin.presentation.UiRunner
import com.berlin.presentation.io.Viewer

class GetAllProjectsUi(
    private val getAllProjectsUseCase: GetAllProjectsUseCase,
    private val viewer: Viewer,
) : PermissionedUiRunner {
    override val id: Int = 13
    override val label: String = "Get All Projects"

    override fun isAllowed(permission: Permission) = permission.getAllProjects

    override fun run() {
        displayProjects()
    }

    private fun displayProjects() {
        getAllProjectsUseCase.getAllProjects().let { projects ->
            when {
                projects.isNotEmpty() -> {
                    projects.forEach { project ->
                        viewer.show("Project ID: ${project.id}, Title: ${project.name}")
                    }
                }
                else -> {
                    viewer.show("No projects available.\n")
                }
            }
        }
    }
}