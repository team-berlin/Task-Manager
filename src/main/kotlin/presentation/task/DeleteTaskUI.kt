package com.berlin.presentation.task

import com.berlin.domain.exception.InputCancelledException
import com.berlin.domain.exception.InvalidSelectionException
import com.berlin.domain.usecase.task.DeleteTaskUseCase
import com.berlin.presentation.helper.choose
import org.berlin.data.DummyData
import org.berlin.presentation.UiRunner
import org.berlin.presentation.input_output.Reader
import org.berlin.presentation.input_output.Viewer

class DeleteTaskUI(
    private val deleteTask: DeleteTaskUseCase,
    private val viewer: Viewer,
    private val reader: Reader,
) : UiRunner {

    override val id: Int = 3
    override val label: String = "Delete task"

    override fun run() {
        try {
            val task = choose(
                "Tasks", DummyData.tasks, { "${it.id} – ${it.title}" }, viewer, reader)

            viewer.show("Type Y to confirm deletion:")
            if (!reader.read().equals("y", true)) throw InputCancelledException("")

            deleteTask(task.id).onSuccess { DummyData.tasks.remove(task); viewer.show("Deleted.") }
                .onFailure { viewer.show("${it.message}") }

        } catch (ex: InputCancelledException) {
            viewer.show("Cancelled.")
        } catch (ex: InvalidSelectionException) {
            viewer.show("${ex.message}")
        }
    }
}
