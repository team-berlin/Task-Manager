package com.berlin.presentation.task

import com.berlin.data.DummyData
import com.berlin.domain.exception.InputCancelledException
import com.berlin.domain.exception.InvalidSelectionException
import com.berlin.domain.exception.InvalidTaskTitle
import com.berlin.domain.exception.TaskAlreadyExistsException
import com.berlin.domain.model.Project
import com.berlin.domain.model.User
import com.berlin.domain.usecase.task.CreateTaskUseCase
import com.berlin.presentation.UiRunner
import com.berlin.presentation.helper.choose
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer

class CreateTaskUI(
    private val createTask: CreateTaskUseCase,
    private val currentUser: User,
    private val viewer: Viewer,
    private val reader: Reader,
) : UiRunner {

    override val id: Int = 1
    override val label: String = "Create task"

    override fun run() {
        try {
            val project = selectProject()
            val state = selectState(project)
            val assignee = selectUser()
            val (title, desc) = askTitleAndDescription()

            createTask(
                project.id, title, desc, state.id, currentUser.id, assignee.id
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
        title = "Projects", elements = DummyData.projects, labelOf = { it.name }, viewer = viewer, reader = reader
    )

    private fun selectState(project: Project) = choose(
        title = "States for ${project.name}",
        elements = DummyData.states.filter { it.projectId == project.id },
        labelOf = { it.name },
        viewer = viewer,
        reader = reader
    )

    private fun selectUser(): User = choose(
        title = "Users", elements = DummyData.users, labelOf = { it.userName }, viewer = viewer, reader = reader
    )

    private fun askTitleAndDescription(): Pair<String, String?> {
        viewer.show("Enter title:")
        val title = reader.read()?.trim().orEmpty()
        if (title.isEmpty()) throw InvalidSelectionException("Title cannot be empty.")
        viewer.show("Description (optional):")
        return title to reader.read()?.trim()
    }
}
