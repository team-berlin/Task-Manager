package com.berlin.presentation.task

import com.berlin.domain.exception.InvalidProjectIdException
import com.berlin.domain.model.Project
import com.berlin.domain.model.Task
import com.berlin.domain.model.TaskState
import com.berlin.domain.usecase.project.GetAllProjectsUseCase
import com.berlin.domain.usecase.task_state.GetAllTaskStatesByProjectIdUseCase
import com.berlin.domain.usecase.task.GetTasksByProjectUseCase
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class GetTasksByProjectIdUITest {

    private lateinit var viewer: Viewer
    private lateinit var reader: Reader
    private lateinit var getTasksByProjectUseCase: GetTasksByProjectUseCase
    private lateinit var getAllProjectsUseCase: GetAllProjectsUseCase
    private lateinit var getAllStatesByProjectIdUseCase: GetAllTaskStatesByProjectIdUseCase
    private lateinit var getTasksByProjectIdUI: GetTasksByProjectIdUI

    private val printed = mutableListOf<String>()

    private val project = Project(
        id = "P1", title = "Core", description = null, statesId = listOf("S1"), emptyList()
    )
    private val stateTodo = TaskState(id = "S1", name = "TODO", projectId = "P1")
    private val stateInProg = TaskState(id = "S2", name = "IN_PROGRESS", projectId = "P1")
    private val task = Task(
        id = "T1",
        projectId = "P1",
        title = "Feature",
        description = null,
        stateId = "S1",
        assignedToUserId = "U1",
        createByUserId = "U1"
    )

    @BeforeEach
    fun setUp() {
        viewer = mockk(relaxed = true) {
            every { show(capture(printed)) } just Runs
        }
        reader = mockk()
        getTasksByProjectUseCase = mockk()
        getAllProjectsUseCase = mockk()
        getAllStatesByProjectIdUseCase = mockk()

        every { getAllProjectsUseCase.invoke() } returns listOf(project)

        getTasksByProjectIdUI = GetTasksByProjectIdUI(
            getTasksByProjectUseCase = getTasksByProjectUseCase,
            getAllProjectsUseCase = getAllProjectsUseCase,
            getAllTaskStatesByProjectIdUseCase = getAllStatesByProjectIdUseCase,
            viewer = viewer,
            reader = reader
        )

        printed.clear()
    }

    private fun stubProjectChoice(input: String) {
        every { reader.read() } returns input
    }

    @Test
    fun `success shows swimlane with one task in the TODO lane`() {
        stubProjectChoice("1")
        every { getTasksByProjectUseCase.invoke("P1") } returns listOf(task)
        every { getAllStatesByProjectIdUseCase.invoke("P1") } returns listOf(stateTodo, stateInProg)

        getTasksByProjectIdUI.run()

        assertThat(printed).contains("\n=== Tasks for project P1 ===")
        assertThat(printed).contains("- T1: Feature  → U1")
    }

    @Test
    fun `no states prints No states found message`() {
        stubProjectChoice("1")
        every { getTasksByProjectUseCase.invoke("P1") } returns emptyList()
        every { getAllStatesByProjectIdUseCase.invoke("P1") } returns emptyList()

        getTasksByProjectIdUI.run()

        assertThat(printed.last()).isEqualTo("No states found for that project.")
    }

    @Test
    fun `empty task list prints placeholder in each state lane`() {
        stubProjectChoice("1")
        every { getTasksByProjectUseCase.invoke("P1") } returns emptyList()
        every { getAllStatesByProjectIdUseCase.invoke("P1") } returns listOf(stateTodo)

        getTasksByProjectIdUI.run()

        assertThat(printed).contains("  (no tasks)")
    }

    @Test
    fun `user cancellation prints Cancelled`() {
        stubProjectChoice("X")

        getTasksByProjectIdUI.run()

        assertThat(printed.last()).isEqualTo("Cancelled.")
        verify { getTasksByProjectUseCase wasNot Called }
    }

    @Test
    fun `invalid selection prints Invalid selection`() {
        stubProjectChoice("99")

        getTasksByProjectIdUI.run()

        assertThat(printed.last()).isEqualTo("Invalid selection")
        verify { getTasksByProjectUseCase wasNot Called }
    }

    @Test
    fun `propagates exception on general failure`() {
        stubProjectChoice("1")
        every { getTasksByProjectUseCase.invoke("P1") } throws RuntimeException("boom")

        assertThrows<RuntimeException> { getTasksByProjectIdUI.run() }
        verify(exactly = 1) { getTasksByProjectUseCase.invoke("P1") }
    }

    @Test
    fun `throws InvalidProjectIdException prints invalid project id`() {
        stubProjectChoice("1")
        every { getTasksByProjectUseCase.invoke("P1") } throws InvalidProjectIdException("bad id")

        getTasksByProjectIdUI.run()

        assertThat(printed.last()).isEqualTo("invalid project id")
        verify(exactly = 1) { getTasksByProjectUseCase.invoke("P1") }
    }
}
