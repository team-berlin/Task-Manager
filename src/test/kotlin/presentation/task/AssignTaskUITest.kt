package com.berlin.presentation.task

import com.berlin.data.DummyData
import com.berlin.domain.exception.InvalidAssigneeException
import com.berlin.domain.model.Task
import com.berlin.domain.model.User
import com.berlin.domain.usecase.task.AssignTaskUseCase
import com.berlin.domain.usecase.task.GetAllTasksUseCase
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AssignTaskUITest {

    private val printed = mutableListOf<String>()
    private val viewer: Viewer = mockk(relaxed = true) {
        coEvery { show(capture(printed)) } just Runs
    }

    private val reader: Reader = mockk()
    private val assignTaskUC: AssignTaskUseCase = mockk()
    private val getAllTasks: GetAllTasksUseCase = mockk()

    private lateinit var task: Task
    private lateinit var newAssignee: User

    @BeforeEach
    fun setUp() {
        DummyData.tasks.clear()

        newAssignee = DummyData.users[1]
        task = Task(
            id = "1",
            projectId = "P1",
            title = "Demo",
            description = null,
            stateId = "TODO",
            assignedToUserId = DummyData.users[0].id,
            createByUserId = DummyData.users[0].id
        )
        DummyData.tasks += task

        coEvery { getAllTasks.invoke() } returns listOf(task)
    }

    @Test
    fun `repository update is called with new assignee`() = runTest {
        coEvery { reader.read() } returnsMany listOf("1", "2")
        coEvery {
            assignTaskUC.invoke(
                task.id,
                newAssignee.id
            )
        }.returns(Result.success(task.copy(assignedToUserId = newAssignee.id)))

        AssignTaskUI(assignTaskUC, getAllTasks, viewer, reader).run()

        coVerify(exactly = 1) { assignTaskUC.invoke(task.id, newAssignee.id) }
        assertThat(printed).contains("Assigned to ${newAssignee.userName}")
    }

    @Test
    fun `user cancels in first chooser`() = runTest {
        coEvery { reader.read() } returns "X"

        AssignTaskUI(assignTaskUC, getAllTasks, viewer, reader).run()

        coVerify(exactly = 0) { assignTaskUC.invoke(any(), any()) }
        assertThat(printed.last()).contains("Cancelled.")
    }

    @Test
    fun `user cancels in second chooser`() = runTest {
        coEvery { reader.read() } returnsMany listOf("1", "X")

        AssignTaskUI(assignTaskUC, getAllTasks, viewer, reader).run()

        coVerify(exactly = 0) { assignTaskUC.invoke(any(), any()) }
        assertThat(printed.last()).contains("Cancelled.")
    }

    @Test
    fun `error from use case is shown to the user`() = runTest {
        coEvery { reader.read() } returnsMany listOf("1", "2")
        val boom = IllegalStateException("cant assign")
        coEvery { assignTaskUC.invoke(task.id, newAssignee.id) }.returns(Result.failure(boom))

        AssignTaskUI(assignTaskUC, getAllTasks, viewer, reader).run()

        coVerify(exactly = 1) { assignTaskUC.invoke(task.id, newAssignee.id) }
        assertThat(printed.last()).contains("cant assign")
    }

    @Test
    fun `invalid index prints error message`() = runTest {
        coEvery { reader.read() } returns "36"

        AssignTaskUI(assignTaskUC, getAllTasks, viewer, reader).run()

        coVerify(exactly = 0) { assignTaskUC.invoke(any(), any()) }
        assertThat(printed.last()).contains("Invalid selection")
    }

    @Test
    fun `throws and shows InvalidAssigneeException`() = runTest {
        coEvery { reader.read() } returnsMany listOf("1", "2")
        coEvery { assignTaskUC.invoke(task.id, newAssignee.id) }.throws(InvalidAssigneeException("nope"))

        AssignTaskUI(assignTaskUC, getAllTasks, viewer, reader).run()

        coVerify(exactly = 1) { assignTaskUC.invoke(task.id, newAssignee.id) }
        assertThat(printed.last()).contains("Invalid assignee")
    }

    @Test
    fun `on failure with null message shows default assignment failed`() = runTest {
        coEvery { reader.read() } returnsMany listOf("1", "2")

        coEvery {
            assignTaskUC.invoke(
                task.id,
                newAssignee.id
            )
        } returns Result.failure(RuntimeException("Assignment failed"))

        AssignTaskUI(assignTaskUC, getAllTasks, viewer, reader).run()

        coVerify(exactly = 1) { assignTaskUC.invoke(task.id, newAssignee.id) }
        assertThat(printed.last()).isEqualTo("Assignment failed")
    }

}
