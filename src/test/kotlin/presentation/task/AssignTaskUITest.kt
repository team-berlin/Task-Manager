package com.berlin.presentation.task

import com.berlin.domain.exception.InvalidAssigneeException
import com.berlin.domain.model.Task
import com.berlin.domain.model.user.User
import com.berlin.domain.usecase.authService.GetAllUsersUseCase
import com.berlin.domain.usecase.task.AssignTaskUseCase
import com.berlin.domain.usecase.task.GetAllTasksUseCase
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AssignTaskUITest {

    private lateinit var viewer: Viewer
    private lateinit var reader: Reader
    private lateinit var assignTaskUseCase: AssignTaskUseCase
    private lateinit var getAllTasksUseCase: GetAllTasksUseCase
    private lateinit var getAllUsersUseCase: GetAllUsersUseCase
    private lateinit var assignTaskUI: AssignTaskUI

    private val printed = mutableListOf<String>()

    private val task = Task(
        id = "T1",
        projectId = "P1",
        title = "Demo",
        description = null,
        stateId = "TODO",
        assignedToUserId = "U1",
        createByUserId = "U1"
    )
    private val user1 = User(id = "U1", userName = "alice", role = User.UserRole.MATE)
    private val user2 = User(id = "U2", userName = "bob", role = User.UserRole.MATE)

    @BeforeEach
    fun setUp() {
        viewer = mockk(relaxed = true) {
            every { show(capture(printed)) } just Runs
        }
        reader = mockk()
        assignTaskUseCase = mockk()
        getAllTasksUseCase = mockk()
        getAllUsersUseCase = mockk()

        every { getAllTasksUseCase.invoke() } returns listOf(task)
        every { getAllUsersUseCase.invoke() } returns listOf(user1, user2)

        assignTaskUI = AssignTaskUI(assignTaskUseCase, getAllTasksUseCase, getAllUsersUseCase, viewer, reader)
        printed.clear()
    }

    /** drive the two reader.read() calls: task‐choice, then assignee‐choice */
    private fun stubReads(vararg inputs: String) {
        every { reader.read() } returnsMany inputs.toList()
    }

    @Test
    fun `success prints Assigned to userName`() {
        stubReads("1", "2")
        every { assignTaskUseCase.invoke(task.id, user2.id) } returns task.copy(assignedToUserId = user2.id)

        assignTaskUI.run()

        verify(exactly = 1) { assignTaskUseCase.invoke("T1", "U2") }
        assertThat(printed.last()).isEqualTo("Assigned to bob")
    }

    @Test
    fun `throws InvalidAssigneeException prints Invalid assignee`() {
        stubReads("1", "2")
        every { assignTaskUseCase.invoke(task.id, user2.id) } throws InvalidAssigneeException("nope")

        assignTaskUI.run()

        verify(exactly = 1) { assignTaskUseCase.invoke("T1", "U2") }
        assertThat(printed.last()).isEqualTo("Invalid assignee")
    }

    @Test
    fun `cancel in task chooser prints Cancelled`() {
        every { reader.read() } returns "X"

        assignTaskUI.run()

        verify { assignTaskUseCase wasNot Called }
        assertThat(printed.last()).isEqualTo("Cancelled.")
    }

    @Test
    fun `cancel in assignee chooser prints Cancelled`() {
        stubReads("1", "X")

        assignTaskUI.run()

        verify { assignTaskUseCase wasNot Called }
        assertThat(printed.last()).isEqualTo("Cancelled.")
    }

    @Test
    fun `invalid task index prints Invalid selection`() {
        every { reader.read() } returns "99"

        assignTaskUI.run()

        verify { assignTaskUseCase wasNot Called }
        assertThat(printed.last()).isEqualTo("Invalid selection")
    }
}
