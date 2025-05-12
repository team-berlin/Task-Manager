package com.berlin.presentation.project

import com.berlin.domain.exception.InvalidProjectIdException
import com.berlin.domain.exception.ProjectNotFoundException
import com.berlin.domain.model.Permission
import com.berlin.domain.usecase.project.GetProjectByIdUseCase
import com.berlin.domain.model.Project
import com.berlin.presentation.PermissionedUiRunner
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer

class GetProjectByIdUi(
    private val getProjectByIdUseCase: GetProjectByIdUseCase,
    private val viewer: Viewer,
    private val reader: Reader
) : PermissionedUiRunner {
    override val id: Int = 4
    override val label: String = "View Project Details"

    override fun isAllowed(permission: Permission) = permission.getTasksByProjectId

    override fun run() {
        try {
            viewer.show("Enter project ID:")
            val projectId = reader.read()?.trim().orEmpty()
            val project = getProjectByIdUseCase.getProjectById(projectId)

            showProject(project)

        } catch (ex: InvalidProjectIdException) {
            viewer.show("Invalid project ID")
        } catch (ex: ProjectNotFoundException) {
            viewer.show("No project found with ID")
        } catch (ex: Exception) {
            viewer.show(ex.message ?: "Lookup failed")
        }
    }

    private fun showProject(p: Project) {
        viewer.show("ID: ${p.id}")
        viewer.show("Title: ${p.title}")
        viewer.show("Description: ${p.description ?: "(none)"}")
        showStates(p)
        showTasks(p)
    }


    private fun showStates(project: Project) {
        val states = project.statesId.orEmpty()
        if (states.isEmpty()) {
            viewer.show("No states defined for this project.")
        } else {
            viewer.show("States:")
            states.forEach { stateId ->
                viewer.show(" - [$stateId] $stateId")
            }
        }
    }

    private fun showTasks(project: Project) {
        val tasks = project.tasksId.orEmpty()
        if (tasks.isEmpty()) {
            viewer.show("\nNo tasks defined for this project.")
        } else {
            viewer.show("\nTasks:")
            tasks.forEach { taskId ->
                viewer.show(" - Task ID: $taskId")
            }
        }
    }


}