package com.berlin.presentation.task

import com.berlin.domain.exception.InputCancelledException
import com.berlin.domain.exception.InvalidProjectIdException
import com.berlin.domain.exception.InvalidSelectionException
import com.berlin.domain.model.Permission
import com.berlin.domain.model.Task
import com.berlin.domain.usecase.project.GetAllProjectsUseCase
import com.berlin.domain.usecase.state.GetAllStatesByProjectIdUseCase
import com.berlin.domain.usecase.task.GetTasksByProjectUseCase
import com.berlin.presentation.PermissionedUiRunner
import com.berlin.presentation.helper.choose
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer

class GetTasksByProjectIdUI(
    private val getTasksByProjectUseCase: GetTasksByProjectUseCase,
    private val getAllProjectsUseCase: GetAllProjectsUseCase,
    private val getAllStatesByProjectIdUseCase: GetAllStatesByProjectIdUseCase,
    private val viewer: Viewer,
    private val reader: Reader,
) : PermissionedUiRunner {

    override val id: Int = 4
    override val label: String = "View tasks by project"

    override fun isAllowed(permission: Permission) = permission.getTasksByProjectId

    override fun run() {
        try {
            val project = choose(
                title = "Projects",
                elements = getAllProjectsUseCase(),
                labelOf = { it.title },
                viewer = viewer,
                reader = reader
            )

            val tasks = getTasksByProjectUseCase(project.id)
            showSwimLaneFor(project.id, tasks)


        } catch (ex: InputCancelledException) {
            viewer.show("Cancelled.")
        } catch (ex: InvalidSelectionException) {
            viewer.show("Invalid selection")
        } catch (ex: InvalidProjectIdException) {
            viewer.show("invalid project id")
        }
    }

    private fun showSwimLaneFor(projectId: String, tasks: List<Task>) {
        val states = getAllStatesByProjectIdUseCase(projectId)
        if (states.isEmpty()) {
            viewer.show("No states found for that project.")
            return
        }

        viewer.show("\n=== Tasks for project $projectId ===")
        states.forEach { state ->
            viewer.show("\n[${state.name}]")
            tasksInState(tasks, state.id).forEach { line ->
                viewer.show(line)
            }
        }
    }

    private fun tasksInState(all: List<Task>, stateId: String): List<String> {
        val here = all.filter { it.stateId == stateId }
        if (here.isEmpty()) return listOf("  (no tasks)")
        return here.map { "- ${it.id}: ${it.title}  → ${it.assignedToUserId}" }
    }
}
