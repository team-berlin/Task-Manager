package com.berlin.presentation.task

import com.berlin.domain.exception.InputCancelledException
import com.berlin.domain.exception.InvalidSelectionException
import com.berlin.domain.exception.InvalidTaskTitle
import com.berlin.domain.exception.TaskAlreadyExistsException
import com.berlin.domain.model.Permission
import com.berlin.domain.model.Project
import com.berlin.domain.model.User
import com.berlin.domain.usecase.authService.GetAllUsersUseCase
import com.berlin.domain.usecase.project.GetAllProjectsUseCase
import com.berlin.domain.usecase.state.GetAllStatesByProjectIdUseCase
import com.berlin.domain.usecase.task.CreateTaskUseCase
import com.berlin.presentation.PermissionedUiRunner
import com.berlin.presentation.helper.choose
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import data.UserCache

class CreateTaskUI(
    private val createTask: CreateTaskUseCase,
    private val cashedUser: UserCache,
    private val getAllProjectsUseCase: GetAllProjectsUseCase,
    private val getAllUsersUseCase: GetAllUsersUseCase,
    private val getAllStatesByProjectIdUseCase: GetAllStatesByProjectIdUseCase,
    private val viewer: Viewer,
    private val reader: Reader,
) : PermissionedUiRunner {

    override val id: Int = 1
    override val label: String = "Create task"

    override fun isAllowed(permission: Permission) = permission.createTask

    override fun run() {
        try {
            val project = selectProject()
            val state = selectState(project)
            val assignee = selectUser()
            val (title, desc) = askTitleAndDescription()

            createTask(
                project.id, title, desc, state.id, cashedUser.currentUser.id, assignee.id
            ).onSuccess { viewer.show("Task created: id=${it.id}") }
                .onFailure { viewer.show(it.message ?: "Creation failed") }

        } catch (ex: InputCancelledException) {
            viewer.show("Cancelled.")
        } catch (ex: InvalidSelectionException) {
            viewer.show("Invalid selection")
        } catch (ex: InvalidTaskTitle) {
            viewer.show("Invalid task title")
        } catch (ex: TaskAlreadyExistsException) {
            viewer.show("the task already existed")
        }
    }

    private fun selectProject() = choose(
        title = "Projects", elements = getAllProjectsUseCase.getAllProjects(), labelOf = { it.name }, viewer = viewer, reader = reader
    )

    private fun selectState(project: Project) = choose(
        title = "States for ${project.name}",
        elements = getAllStatesByProjectIdUseCase.getAllStatesByProjectId(project.id).getOrNull() ?: emptyList(),
        labelOf = { it.name },
        viewer = viewer,
        reader = reader
    )

    private fun selectUser(): User = choose(
        title = "Users", elements = getAllUsersUseCase.getAllUsers().getOrNull() ?: emptyList(), labelOf = { it.userName }, viewer = viewer, reader = reader
    )

    private fun askTitleAndDescription(): Pair<String, String?> {
        viewer.show("Enter title:")
        val title = reader.read()?.trim().orEmpty()
        if (title.isEmpty()) throw InvalidSelectionException("Title cannot be empty.")
        viewer.show("Description (optional):")
        return title to reader.read()?.trim()
    }
}
