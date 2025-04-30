package com.berlin.presentation.task


import com.berlin.domain.exception.InputCancelledException
import com.berlin.domain.exception.InvalidSelectionException
import com.berlin.domain.usecase.task.AssignTaskUseCase
import com.berlin.presentation.helper.choose
import org.berlin.data.DummyData
import org.berlin.presentation.UiRunner
import org.berlin.presentation.input_output.Reader
import org.berlin.presentation.input_output.Viewer

class AssignTaskUI(
    private val assignTask: AssignTaskUseCase,
    private val viewer: Viewer,
    private val reader: Reader
) : UiRunner {

    override val id : Int = 2
    override val label : String = "Assign task"

    override fun run() {
        try {
            val task = selectTask()
            val assignee = choose("Users", DummyData.users,
                { it.userName }, viewer, reader)

            assignTask(task.id, assignee)
                .onSuccess { viewer.show("Assigned to ${assignee.userName}") }
                .onFailure { viewer.show("${it.message}") }

        } catch (ex: InputCancelledException) { viewer.show("Cancelled.") }
        catch (ex: InvalidSelectionException) { viewer.show("${ex.message}") }
    }

    private fun selectTask() = choose(
        "Tasks",
        DummyData.tasks,
        { "${it.id} – ${it.title}" },
        viewer, reader
    )
}