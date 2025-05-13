package com.berlin.presentation.task

import com.berlin.domain.exception.InvalidTaskIdException
import com.berlin.domain.model.Task
import com.berlin.domain.usecase.task.DeleteTaskUseCase
import com.berlin.domain.usecase.task.GetAllTasksUseCase
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DeleteTaskUITest {

    private val printed = mutableListOf<String>()
    private lateinit var viewer: Viewer
    private lateinit var reader: Reader
    private lateinit var deleteUC: DeleteTaskUseCase
    private lateinit var getAllTasks: GetAllTasksUseCase
    private lateinit var ui: DeleteTaskUI

    private val task = Task(
        id = "T1",
        projectId = "P1",
        title = "Demo",
        description = null,
        stateId = "TODO",
        assignedToUserId = "U1",
        createByUserId = "U1"
    )

    @BeforeEach
    fun setUp() {
        printed.clear()

        viewer = mockk(relaxed = true) {
            every { show(capture(printed)) } just Runs
        }
        reader = mockk()
        deleteUC = mockk()
        getAllTasks = mockk()

        every { getAllTasks.invoke() } returns listOf(task)

        ui = DeleteTaskUI(
            deleteTaskUseCase = deleteUC, getAllTasksUseCase = getAllTasks, viewer = viewer, reader = reader
        )
    }

    @Test
    fun `deletes task and prints confirmation`() {
        // choose index "1", then confirm with "y"
        every { reader.read() } returnsMany listOf("1", "y")
        // use‐case now returns a String to show
        every { deleteUC.invoke(task.id) } returns "Deleted."

        ui.run()

        verify(exactly = 1) { deleteUC.invoke(task.id) }
        assertThat(printed.last()).contains("Deleted.")
    }

    @Test
    fun `user aborts deletion at confirmation`() {
        // choose "1", then "n" to abort
        every { reader.read() } returnsMany listOf("1", "n")

        ui.run()

        verify(exactly = 0) { deleteUC.invoke(any()) }
        assertThat(printed.last()).contains("Cancelled.")
    }

    @Test
    fun `user cancels in chooser`() {
        // reader.read() = "X" → choose(...) throws InputCancelledException
        every { reader.read() } returns "X"

        ui.run()

        verify(exactly = 0) { deleteUC.invoke(any()) }
        assertThat(printed.last()).contains("Cancelled.")
    }

    @Test
    fun `failure from use case is shown`() {
        // choose "1", confirm "y"
        every { reader.read() } returnsMany listOf("1", "y")
        // use‐case returns an error message string
        every { deleteUC.invoke(task.id) } returns "cannot delete"

        ui.run()

        verify(exactly = 1) { deleteUC.invoke(task.id) }
        assertThat(printed.last()).contains("cannot delete")
    }

    @Test
    fun `invalid index prints error message`() {
        // bad task index
        every { reader.read() } returns "99"

        ui.run()

        verify(exactly = 0) { deleteUC.invoke(any()) }
        assertThat(printed.last()).contains("Invalid selection")
    }

    @Test
    fun `throws and shows InvalidTaskIdException from use case`() {
        // choose "1", confirm "y"
        every { reader.read() } returnsMany listOf("1", "y")
        // use‐case throws ID exception
        every { deleteUC.invoke(task.id) } throws InvalidTaskIdException("bad id")

        ui.run()

        verify(exactly = 1) { deleteUC.invoke(task.id) }
        assertThat(printed.last()).contains("invalid task id")
    }
}
