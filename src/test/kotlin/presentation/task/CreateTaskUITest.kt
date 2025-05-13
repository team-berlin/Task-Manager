package com.berlin.presentation.task

import com.berlin.data.DummyData
import com.berlin.domain.exception.InvalidTaskTitle
import com.berlin.domain.exception.TaskAlreadyExistsException
import com.berlin.domain.model.Task
import com.berlin.domain.model.TaskState
import com.berlin.domain.model.user.User
import com.berlin.domain.usecase.authService.GetAllUsersUseCase
import com.berlin.domain.usecase.project.GetAllProjectsUseCase
import com.berlin.domain.usecase.state.GetAllStatesByProjectIdUseCase
import com.berlin.domain.usecase.task.CreateTaskUseCase
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import com.google.common.truth.Truth.assertThat
import data.UserCache
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CreateTaskUITest {

    private val printed = mutableListOf<String>()
    private val viewer: Viewer = mockk(relaxed = true) {
        every { show(capture(printed)) } just Runs
    }

    private lateinit var getAllProjectsUseCase: GetAllProjectsUseCase
    private lateinit var getAllUsersUseCase: GetAllUsersUseCase
    private lateinit var getAllStatesByProjectIdUseCase: GetAllStatesByProjectIdUseCase
    private val reader: Reader = mockk()
    private val createTaskUseCase: CreateTaskUseCase = mockk()
    private val userCache: UserCache = mockk()
    private val currentUser: User = DummyData.users.first()
    private lateinit var createTaskUI: CreateTaskUI

    private val sampleStates = listOf(
        TaskState("S1", "TODO", "P1"),
        TaskState("S2", "IN_PROGRESS", "P1"),
        TaskState("S3", "REVIEW", "P1"),
        TaskState("S4", "DONE", "P1")
    )

    @BeforeEach
    fun setUp() {
        // reset printed buffer and dummy storage
        printed.clear()
        DummyData.tasks.clear()

        every { userCache.currentUser } returns currentUser

        getAllProjectsUseCase = mockk()
        every { getAllProjectsUseCase() } returns DummyData.projects

        getAllUsersUseCase = mockk()
        every { getAllUsersUseCase() } returns DummyData.users

        getAllStatesByProjectIdUseCase = mockk()

        createTaskUI = CreateTaskUI(
            createTaskUseCase = createTaskUseCase,
            cashedUser = userCache,
            getAllProjectsUseCase = getAllProjectsUseCase,
            getAllUsersUseCase = getAllUsersUseCase,
            getAllStatesByProjectIdUseCase = getAllStatesByProjectIdUseCase,
            viewer = viewer,
            reader = reader
        )
    }

    @Test
    fun `creates task and prints success`() {
        every { getAllStatesByProjectIdUseCase("P1") } returns sampleStates

        every { reader.read() } returnsMany listOf("1", "1", "1", "Feature X", "")

        every {
            createTaskUseCase.invoke(any(), any(), any(), any(), any(), any())
        } answers {
            Task(
                id = "T42",
                projectId = firstArg(),
                title = secondArg(),
                description = arg(2),
                stateId = arg(3),
                assignedToUserId = arg(5),
                createByUserId = arg(4)
            )
        }

        createTaskUI.run()

        verify {
            createTaskUseCase.invoke(
                "P1", "Feature X", "", "S1", currentUser.id, DummyData.users[0].id
            )
        }
        assertThat(printed.last()).contains("Task created: id=T42")
    }

    @Test
    fun `prints Cancelled when user aborts`() {
        every { reader.read() } returns "X"

        createTaskUI.run()

        verify(exactly = 0) { createTaskUseCase.invoke(any(), any(), any(), any(), any(), any()) }
        assertThat(printed.last()).contains("Cancelled.")
    }

    @Test
    fun `empty title shows invalid selection message`() {
        every { getAllStatesByProjectIdUseCase("P1") } returns sampleStates
        every { reader.read() } returnsMany listOf("1", "1", "1", "")

        createTaskUI.run()

        verify(exactly = 0) { createTaskUseCase.invoke(any(), any(), any(), any(), any(), any()) }
        assertThat(printed.last()).contains("Title cannot be empty.")
    }

    @Test
    fun `invalid project index prints error message`() {
        // bad project choice
        every { reader.read() } returns "79"

        createTaskUI.run()

        verify(exactly = 0) { createTaskUseCase.invoke(any(), any(), any(), any(), any(), any()) }
        assertThat(printed.last().lowercase()).contains("out of range.")
    }

    @Test
    fun `invalid state index prints error message`() {
        every { getAllStatesByProjectIdUseCase("P1") } returns sampleStates
        every { reader.read() } returnsMany listOf("1", "57")

        createTaskUI.run()

        verify(exactly = 0) { createTaskUseCase.invoke(any(), any(), any(), any(), any(), any()) }
        assertThat(printed.last().lowercase()).contains("out of range.")
    }

    @Test
    fun `invalid user index prints error message`() {
        every { getAllStatesByProjectIdUseCase("P1") } returns sampleStates
        every { reader.read() } returnsMany listOf("1", "1", "99")

        createTaskUI.run()

        verify(exactly = 0) { createTaskUseCase.invoke(any(), any(), any(), any(), any(), any()) }
        assertThat(printed.last().lowercase()).contains("out of range.")
    }

    @Test
    fun `throws IllegalStateException when use case throws unexpected error`() {
        every { getAllStatesByProjectIdUseCase("P1") } returns sampleStates
        every { reader.read() } returnsMany listOf("1", "1", "1", "Bug fix", "")

        every {
            createTaskUseCase.invoke(any(), any(), any(), any(), any(), any())
        } throws IllegalStateException("db down")

        assertThrows<IllegalStateException> { createTaskUI.run() }
    }

    @Test
    fun `shows InvalidTaskTitle when use case throws that exception`() {
        every { getAllStatesByProjectIdUseCase("P1") } returns sampleStates
        every { reader.read() } returnsMany listOf("1", "1", "1", "BadTitle", "")

        every {
            createTaskUseCase.invoke(any(), any(), any(), any(), any(), any())
        } throws InvalidTaskTitle("title ruled invalid")

        createTaskUI.run()

        assertThat(printed.last()).contains("Invalid task title")
        verify(exactly = 1) { createTaskUseCase.invoke(any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `shows TaskAlreadyExistsException when use case throws that exception`() {
        every { getAllStatesByProjectIdUseCase("P1") } returns sampleStates
        every { reader.read() } returnsMany listOf("1", "1", "1", "MyTask", "")

        every {
            createTaskUseCase.invoke(any(), any(), any(), any(), any(), any())
        } throws TaskAlreadyExistsException("the task already existed")

        createTaskUI.run()

        assertThat(printed.last()).contains("the task already existed")
        verify(exactly = 1) { createTaskUseCase.invoke(any(), any(), any(), any(), any(), any()) }
    }
}
