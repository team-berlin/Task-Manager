package com.berlin.presentation.task

import com.berlin.data.DummyData
import com.berlin.domain.exception.InvalidAssigneeException
import com.berlin.domain.model.Task
import com.berlin.domain.model.User
import com.berlin.domain.model.UserRole
import com.berlin.domain.usecase.authService.FetchAllUsersUseCase
import com.berlin.domain.usecase.task.AssignTaskUseCase
import com.berlin.domain.usecase.task.GetAllTasksUseCase
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AssignTaskUITest {

    private val printed = mutableListOf<String>()
    private val viewer: Viewer = mockk(relaxed = true) {
        every { show(capture(printed)) } just Runs
    }
    private val reader: Reader = mockk()
    private val assignTaskUC: AssignTaskUseCase = mockk()
    private val getAllTasks: GetAllTasksUseCase = mockk()

    private lateinit var task: Task
    private lateinit var newAssignee: User
    private lateinit var fetchAllUsersUseCase: FetchAllUsersUseCase

    @BeforeEach
    fun setUp() {
        fetchAllUsersUseCase= mockk()
        every { fetchAllUsersUseCase.getAllUsers() }returns Result.success(DummyData.users)
        // reset in-memory data
        DummyData.tasks.clear()
        DummyData.users.clear()

        // populate two users so choose(...) can pick index "1" and "2"
        DummyData.users += User("U1", "alice", "pw", UserRole.MATE)
        DummyData.users += User("U2", "bob",   "pw", UserRole.MATE)

        newAssignee = DummyData.users[1]

        // create one task assigned initially to alice
        task = Task(
            id                 = "1",
            projectId          = "P1",
            title              = "Demo",
            description        = null,
            stateId            = "TODO",
            assignedToUserId   = DummyData.users[0].id,
            createByUserId     = DummyData.users[0].id
        )
        DummyData.tasks += task
        every { getAllTasks.invoke() } returns listOf(task)
        printed.clear()
    }

    @Test
    fun `repository update is called with new assignee`() {
        every { reader.read() } returnsMany listOf("1", "2")
        every {
            assignTaskUC.invoke(task.id, newAssignee.id)
        } returns Result.success(task.copy(assignedToUserId = newAssignee.id))

        AssignTaskUI(assignTaskUC, getAllTasks, fetchAllUsersUseCase,viewer, reader).run()

        verify(exactly = 1) { assignTaskUC.invoke(task.id, newAssignee.id) }
        assertThat(printed.last()).contains("Assigned to ${newAssignee.userName}")
    }

    @Test
    fun `user cancels in first chooser`() {
        every { reader.read() } returns "X"
        AssignTaskUI(assignTaskUC, getAllTasks, fetchAllUsersUseCase,viewer, reader).run()

        verify(exactly = 0) { assignTaskUC.invoke(any(), any()) }
        assertThat(printed.last()).contains("Cancelled.")
    }

    @Test
    fun `user cancels in second chooser`() {
        every { reader.read() } returnsMany listOf("1", "X")

        AssignTaskUI(assignTaskUC, getAllTasks,fetchAllUsersUseCase, viewer, reader).run()

        verify(exactly = 0) { assignTaskUC.invoke(any(), any()) }
        assertThat(printed.last()).contains("Cancelled.")
    }

    @Test
    fun `error from use case is shown to the user`() {
        every { reader.read() } returnsMany listOf("1", "2")
        val boom = IllegalStateException("cant assign")
        every { assignTaskUC.invoke(task.id, newAssignee.id) } returns Result.failure(boom)

        AssignTaskUI(assignTaskUC, getAllTasks,fetchAllUsersUseCase, viewer, reader).run()

        verify(exactly = 1) { assignTaskUC.invoke(task.id, newAssignee.id) }
        assertThat(printed.last()).contains("cant assign")
    }

    @Test
    fun `invalid index prints error message`() {
        every { reader.read() } returns "36"

        AssignTaskUI(assignTaskUC, getAllTasks,fetchAllUsersUseCase, viewer, reader).run()

        verify(exactly = 0) { assignTaskUC.invoke(any(), any()) }
        assertThat(printed.last()).contains("Invalid selection")
    }

    @Test
    fun `throws and shows InvalidAssigneeException`() {
        every { reader.read() } returnsMany listOf("1", "2")
        every { assignTaskUC.invoke(task.id, newAssignee.id) } throws InvalidAssigneeException("nope")

        AssignTaskUI(assignTaskUC, getAllTasks,fetchAllUsersUseCase, viewer, reader).run()

        verify(exactly = 1) { assignTaskUC.invoke(task.id, newAssignee.id) }
        assertThat(printed.last()).contains("Invalid assignee")
    }

    @Test
    fun `on failure with null message shows default assignment failed`() {
        every { reader.read() } returnsMany listOf("1", "2")
        every { assignTaskUC.invoke(task.id, newAssignee.id) } returns Result.failure(RuntimeException("Assignment failed"))

        AssignTaskUI(assignTaskUC, getAllTasks,fetchAllUsersUseCase, viewer, reader).run()

        verify(exactly = 1) { assignTaskUC.invoke(task.id, newAssignee.id) }
        assertThat(printed.last()).isEqualTo("Assignment failed")
    }
}
