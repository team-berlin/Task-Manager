package com.berlin.presentation.task

import com.berlin.data.DummyData
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
    private val viewer: Viewer = mockk(relaxed = true) {
        every { show(capture(printed)) } just Runs
    }
    private val reader: Reader = mockk()
    private val deleteUC: DeleteTaskUseCase = mockk()
    private val getAllTasks: GetAllTasksUseCase = mockk()

    private lateinit var task: Task
    private lateinit var ui: DeleteTaskUI

    @BeforeEach
    fun setUp() {
        DummyData.tasks.clear()
        printed.clear()

        task = Task(
            id = "T1",
            projectId = "P1",
            title = "Demo",
            description = null,
            stateId = "TODO",
            assignedToUserId = "U1",
            createByUserId = "U1"
        )
        DummyData.tasks += task

        every { getAllTasks.invoke() } returns listOf(task)

        ui = DeleteTaskUI(deleteUC, getAllTasks, viewer, reader)
    }

    @Test
    fun `deletes task and prints confirmation`() {
        every { reader.read() } returnsMany listOf("1", "y")
        every { deleteUC.invoke(task.id) } returns Result.success(Unit)

        ui.run()

        verify(exactly = 1) { deleteUC.invoke(task.id) }
        assertThat(DummyData.tasks).doesNotContain(listOf(task))
        assertThat(printed.last()).contains("Deleted.")
    }

    @Test
    fun `user aborts deletion at confirmation`() {
        every { reader.read() } returnsMany listOf("1", "n")

        ui.run()

        verify(exactly = 0) { deleteUC.invoke(any()) }
        assertThat(DummyData.tasks).contains(task)
        assertThat(printed.last()).contains("Cancelled.")
    }

    @Test
    fun `user cancels in chooser`() {
        every { reader.read() } returns "X"

        ui.run()

        verify(exactly = 0) { deleteUC.invoke(any()) }
        assertThat(DummyData.tasks).contains(task)
        assertThat(printed.last()).contains("Cancelled.")
    }

    @Test
    fun `failure from use case is shown`() {
        every { reader.read() } returnsMany listOf("1", "y")
        every { deleteUC.invoke(task.id) } returns Result.failure(IllegalStateException("cannot delete"))

        ui.run()

        verify(exactly = 1) { deleteUC.invoke(task.id) }
        assertThat(DummyData.tasks).contains(task)
        assertThat(printed.last()).contains("cannot delete")
    }

    @Test
    fun `invalid index prints error message`() {
        every { reader.read() } returns "99"

        ui.run()

        verify(exactly = 0) { deleteUC.invoke(any()) }
        assertThat(printed.last()).contains("Invalid selection")
    }

    @Test
    fun `throws and shows InvalidTaskIdException from use case`() {
        every { reader.read() } returnsMany listOf("1", "y")
        every { deleteUC.invoke(task.id) } throws InvalidTaskIdException("bad id")

        ui.run()

        // task should not have been removed
        assertThat(DummyData.tasks).contains(task)
        // and we show the invalid-id message
        assertThat(printed.last()).contains("invalid task id")
        verify(exactly = 1) { deleteUC.invoke(task.id) }
    }
}
