package com.berlin.presentation.project

import com.berlin.domain.exception.InputCancelledException
import com.berlin.domain.exception.InvalidSelectionException
import com.berlin.domain.model.UserRole
import com.berlin.domain.usecase.project.CreateProjectUseCase
import com.berlin.presentation.RoleBasedPermission
import com.berlin.presentation.UiRunner
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer

class CreateProjectUi(
    private val createProject: CreateProjectUseCase,
    private val viewer: Viewer,
    private val reader: Reader,
) : UiRunner, RoleBasedPermission {

    override val allowedRoles = listOf(UserRole.ADMIN)
    override val id: Int = 1
    override val label: String = "Create New Project"


    override fun run() {

        try {
            val (name, description) = askProjectTitleAndDescription()

            createProject.createNewProject(
                projectName = name,
                description = description,
                stateId = null,
                taskId = null
            ).onSuccess { viewer.show("Project created successfully") }
                .onFailure { viewer.show(it.message ?: "Creation failed") }

        } catch (e: InputCancelledException) {
            viewer.show("Project creation cancelled.")
        } catch (e: Exception) {
            viewer.show("Error: ${e.message}")
        }
    }

    private fun askProjectTitleAndDescription(): Pair<String, String?> {
        viewer.show("Enter project name:")
        val name = reader.read()?.trim().orEmpty()
        if (name.isEmpty()) throw InvalidSelectionException("Project name cannot be empty")

        viewer.show("Enter project description (optional):")
        return name to reader.read()?.trim()
    }

}