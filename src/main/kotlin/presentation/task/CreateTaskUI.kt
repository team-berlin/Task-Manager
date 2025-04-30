package org.berlin.presentation.task

import com.berlin.domain.exception.InputCancelledException
import com.berlin.domain.exception.InvalidSelectionException
import com.berlin.domain.model.*
import com.berlin.domain.usecase.task.CreateTaskUseCase
import com.berlin.presentation.helper.choose
import org.berlin.data.DummyData
import org.berlin.presentation.UiRunner
import org.berlin.presentation.input_output.Reader
import org.berlin.presentation.input_output.Viewer

class CreateTaskUI(
    private val createTask: CreateTaskUseCase,
    private val currentUser: User,
    private val viewer : Viewer,
    private val reader : Reader
) : UiRunner {

    override val id    : Int = 1
    override val label : String = "Create task"

    override fun run() {
        try {
            val project  = selectProject()
            val state    = selectState(project)
            val assignee = selectUser()
            val (title,desc) = askTitleAndDescription()

            val result = createTask(
                project.id, title, desc, state.id,
                currentUser, assignee
            )
            result.onSuccess { viewer.show("Task created: id=${it.id}") }
                .onFailure { viewer.show("{it.message}") }

        } catch (ex: InputCancelledException) {
            viewer.show("Cancelled.")
        } catch (ex: InvalidSelectionException) {
            viewer.show("${ex.message}")
        }
    }


    private fun selectProject(): Project =
        choose("Projects", DummyData.projects, { it.name }, viewer, reader)

    private fun selectState(project: Project): State =
        choose(
            "States",
            DummyData.states.filter { it.projectId == project.id },
            { it.name }, viewer, reader
        )

    private fun selectUser(): User =
        choose("Users", DummyData.users, { it.userName }, viewer, reader)

    private fun askTitleAndDescription(): Pair<String,String?> {
        viewer.show("Enter title:")
        val title = reader.read()?.trim().orEmpty()
        if (title.isEmpty()) throw InvalidSelectionException("Title cannot be empty.")
        viewer.show("Description (optional):")
        return title to reader.read()?.trim()
    }
}
