package com.berlin.presentation.task

import com.berlin.domain.exception.InvalidTaskStateException
import com.berlin.domain.exception.TaskNotFoundException
import com.berlin.domain.model.Task
import com.berlin.domain.model.TaskState
import com.berlin.domain.model.user.User
import com.berlin.domain.usecase.state.GetAllStatesUseCase
import com.berlin.domain.usecase.task.ChangeTaskStateUseCase
import com.berlin.domain.usecase.task.GetAllTasksUseCase
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ChangeTaskStateUITest {

    private lateinit var viewer: Viewer
    private lateinit var reader: Reader
    private lateinit var changeTaskStateUseCase: ChangeTaskStateUseCase
    private lateinit var getAllTasksUseCase: GetAllTasksUseCase
    private lateinit var getAllStatesUseCase: GetAllStatesUseCase
    private lateinit var changeTaskStateUI: ChangeTaskStateUI

    private val printed = mutableListOf<String>()

    private val alice = User("U1", "alice", User.UserRole.MATE)
    private val task = Task(
        id = "T1",
        projectId = "P1",
        title = "Demo",
        description = null,
        stateId = "S1",
        assignedToUserId = alice.id,
        createByUserId = alice.id
    )

    private val state1 = TaskState("S1", "TODO", "P1")
    private val state2 = TaskState("S2", "DONE", "P1")

    @BeforeEach
    fun setUp() {
        viewer = mockk(relaxed = true) {
            every { show(capture(printed)) } just Runs
        }
        reader = mockk()
        changeTaskStateUseCase = mockk()
        getAllTasksUseCase = mockk()
        getAllStatesUseCase = mockk()

        every { getAllTasksUseCase.invoke() } returns listOf(task)
        every { getAllStatesUseCase.invoke() } returns listOf(state1, state2)

        changeTaskStateUI = ChangeTaskStateUI(
            changeTaskStateUseCase = changeTaskStateUseCase,
            getAllTasksUseCase = getAllTasksUseCase,
            getAllStatesUseCase = getAllStatesUseCase,
            viewer = viewer,
            reader = reader
        )

        printed.clear()
    }

    @Test
    fun `success moves to chosen state`() {
        every { reader.read() } returnsMany listOf("1", "1")
        every { changeTaskStateUseCase.invoke("T1", "S1") } returns task.copy(stateId = "S1")

        changeTaskStateUI.run()

        verify { changeTaskStateUseCase.invoke("T1", "S1") }
        assertThat(printed.last()).contains("Task T1 moved to TODO")
    }

    @Test
    fun `no states defined prints message and returns`() {
        every { getAllStatesUseCase.invoke() } returns emptyList()
        every { reader.read() } returns "1"

        changeTaskStateUI.run()

        verify { changeTaskStateUseCase wasNot Called }
        assertThat(printed.last()).contains("No states defined for project P1")
    }

    @Test
    fun `cancelling at task chooser prints Cancelled`() {
        every { reader.read() } returns "X"

        changeTaskStateUI.run()

        verify { changeTaskStateUseCase wasNot Called }
        assertThat(printed.last()).contains("Cancelled.")
    }

    @Test
    fun `invalid task index prints Invalid selection`() {
        every { reader.read() } returns "99"

        changeTaskStateUI.run()

        verify { changeTaskStateUseCase wasNot Called }
        assertThat(printed.last()).contains("Invalid selection")
    }

    @Test
    fun `cancelling at state chooser prints Cancelled`() {
        every { reader.read() } returnsMany listOf("1", "X")

        changeTaskStateUI.run()

        verify { changeTaskStateUseCase wasNot Called }
        assertThat(printed.last()).contains("Cancelled.")
    }

    @Test
    fun `invalid state index prints Invalid selection`() {
        every { reader.read() } returnsMany listOf("1", "99")

        changeTaskStateUI.run()

        verify { changeTaskStateUseCase wasNot Called }
        assertThat(printed.last()).contains("Invalid selection")
    }

    @Test
    fun `propagates exception on general failure`() {
        every { reader.read() } returnsMany listOf("1", "1")
        every { changeTaskStateUseCase.invoke("T1", "S1") } throws IllegalStateException("boom")

        assertThrows<IllegalStateException> { changeTaskStateUI.run() }
    }

    @Test
    fun `shows InvalidTaskStateException when use case throws`() {
        every { reader.read() } returnsMany listOf("1", "1")
        every { changeTaskStateUseCase.invoke("T1", "S1") } throws InvalidTaskStateException("bad state")

        changeTaskStateUI.run()

        assertThat(printed.last()).contains("Invalid task state")
    }

    @Test
    fun `shows TaskNotFoundException when use case throws`() {
        every { reader.read() } returnsMany listOf("1", "1")
        every { changeTaskStateUseCase.invoke("T1", "S1") } throws TaskNotFoundException("notfound")

        changeTaskStateUI.run()

        assertThat(printed.last()).contains("Task not found")
    }
}
