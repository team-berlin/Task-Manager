package com.berlin.presentation.task

import com.berlin.domain.exception.InvalidTaskTitle
import com.berlin.domain.exception.TaskNotFoundException
import com.berlin.domain.model.Task
import com.berlin.domain.model.user.User
import com.berlin.domain.usecase.authService.GetAllUsersUseCase
import com.berlin.domain.usecase.task.GetAllTasksUseCase
import com.berlin.domain.usecase.task.UpdateTaskUseCase
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class UpdateTaskUITest {

    private lateinit var viewer: Viewer
    private lateinit var reader: Reader
    private lateinit var updateUC: UpdateTaskUseCase
    private lateinit var getAllTasks: GetAllTasksUseCase
    private lateinit var fetchUsers: GetAllUsersUseCase
    private lateinit var ui: UpdateTaskUI

    // capture all viewer.show calls
    private val printed = mutableListOf<String>()

    private val existing = Task(
        id = "T1",
        projectId = "P1",
        title = "OldTitle",
        description = "OldDesc",
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
        updateUC = mockk()
        getAllTasks = mockk()
        fetchUsers = mockk()

        // stub the two lists
        every { getAllTasks() } returns listOf(existing)
        every { fetchUsers() } returns listOf(user1, user2)

        ui = UpdateTaskUI(
            updateTaskUseCase = updateUC,
            getAllTasksUseCase = getAllTasks,
            getAllUsersUseCase = fetchUsers,
            viewer = viewer,
            reader = reader
        )

        printed.clear()
    }

    /** Helper to drive the successive reads */
    private fun stubReads(vararg inputs: String) {
        every { reader.read() } returnsMany inputs.toList()
    }

    @Test
    fun `update title only`() {
        stubReads("1", "NewTitle", "", "X")
        every {
            updateUC.invoke("T1", title = "NewTitle", description = null, assignedToUserId = null)
        } returns existing.copy(title = "NewTitle")

        ui.run()

        verify {
            updateUC.invoke("T1", title = "NewTitle", description = null, assignedToUserId = null)
        }
        assertThat(printed).contains("Task updated: T1")
    }

    @Test
    fun `update description only`() {
        stubReads("1", "", "NewDesc", "X")
        every {
            updateUC.invoke("T1", title = null, description = "NewDesc", assignedToUserId = null)
        } returns existing.copy(description = "NewDesc")

        ui.run()

        verify {
            updateUC.invoke("T1", title = null, description = "NewDesc", assignedToUserId = null)
        }
        assertThat(printed).contains("Task updated: T1")
    }

    @Test
    fun `update assignee only`() {
        stubReads("1", "", "", "2")
        every {
            updateUC.invoke("T1", title = null, description = null, assignedToUserId = "U2")
        } returns existing.copy(assignedToUserId = "U2")

        ui.run()

        verify {
            updateUC.invoke("T1", title = null, description = null, assignedToUserId = "U2")
        }
        assertThat(printed).contains("Task updated: T1")
    }

    @Test
    fun `nothing changes`() {
        stubReads("1", "", "", "X")
        every {
            updateUC.invoke("T1", title = null, description = null, assignedToUserId = null)
        } returns existing

        ui.run()

        verify {
            updateUC.invoke("T1", title = null, description = null, assignedToUserId = null)
        }
        assertThat(printed).contains("Task updated: T1")
    }

    @Test
    fun `cancel at task chooser prints Cancelled`() {
        every { reader.read() } returns "X"

        ui.run()

        assertThat(printed.last()).isEqualTo("Cancelled.")
        verify { updateUC wasNot Called }
    }

    @Test
    fun `invalid task index prints Invalid selection`() {
        every { reader.read() } returns "foo"

        ui.run()

        assertThat(printed.last()).isEqualTo("Invalid selection")
        verify { updateUC wasNot Called }
    }

    @Test
    fun `invalid assignee index prints Invalid selection`() {
        stubReads("1", "", "", "99")

        ui.run()

        assertThat(printed.last()).isEqualTo("Invalid selection")
        verify { updateUC wasNot Called }
    }

    @Test
    fun `throws IllegalStateException when use case fails unexpectedly`() {
        stubReads("1", "", "", "X")
        every { updateUC.invoke(any(), any(), any(), any()) } throws IllegalStateException("boom")

        assertThrows<IllegalStateException> { ui.run() }
    }

    @Test
    fun `throws InvalidTaskTitle prints Invalid task title`() {
        stubReads("1", "Bad!", "", "X")
        every { updateUC.invoke(any(), any(), any(), any()) } throws InvalidTaskTitle("no digits")

        ui.run()

        assertThat(printed.last()).isEqualTo("Invalid task title")
    }

    @Test
    fun `throws TaskNotFoundException prints Task not founc`() {
        stubReads("1", "", "", "X")
        every { updateUC.invoke(any(), any(), any(), any()) } throws TaskNotFoundException("gone")

        ui.run()

        assertThat(printed.last()).isEqualTo("Task not founc")
    }
}
