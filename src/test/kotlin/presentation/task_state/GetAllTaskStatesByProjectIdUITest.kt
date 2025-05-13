package com.berlin.presentation.task_state

import com.berlin.data.DummyData
import com.berlin.domain.model.Project
import com.berlin.domain.model.TaskState
import com.berlin.domain.usecase.project.GetAllProjectsUseCase
import com.berlin.domain.usecase.task_state.GetAllTaskStatesByProjectIdUseCase
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetAllTaskStatesByProjectIdUITest {

    private val printed = mutableListOf<String>()
    private val viewer: Viewer = mockk(relaxed = true) {
        every { show(capture(printed)) } just Runs
    }
    private val reader: Reader = mockk()
    private lateinit var getAllTaskStatesByProjectIdUseCase: GetAllTaskStatesByProjectIdUseCase
    private lateinit var getAllProjectsUseCase: GetAllProjectsUseCase
    private lateinit var getAllTaskStatesByProjectIdUI: GetAllTaskStatesByProjectIdUI

    @BeforeEach
    fun setUp() {
        getAllProjectsUseCase = mockk()
        every { getAllProjectsUseCase() } returns DummyData.projects

        DummyData.projects.clear()
        DummyData.states.clear()
        printed.clear()

        DummyData.projects += projectP1
        DummyData.states += listOf(stateTodo, stateDone)

        getAllTaskStatesByProjectIdUseCase = mockk()
        getAllTaskStatesByProjectIdUI = GetAllTaskStatesByProjectIdUI(getAllTaskStatesByProjectIdUseCase, getAllProjectsUseCase, viewer, reader)
    }

    @Test
    fun `shows swimlane with states`() {
        every { reader.read() } returns "1"
        every { getAllTaskStatesByProjectIdUseCase(projectIdWithNoStates) } returns
                listOf(
                    stateTodo,
                    stateDone
                )

        getAllTaskStatesByProjectIdUI.run()
        assertThat(printed).contains("\n=== States for project P1 ===")
        assertThat(printed).contains("- S1: TODO")
        assertThat(printed).contains("- S2: DONE")
    }

    @Test
    fun `shows (no states) when project has no states`() {
        every { reader.read() } returns "1"
        every { getAllTaskStatesByProjectIdUseCase(projectIdWithNoStates) } returns emptyList()
        getAllTaskStatesByProjectIdUI.run()
        assertThat(printed).contains("  (no states)")
    }

    @Test
    fun `cancelling input shows Cancelled`() {
        every { reader.read() } returns "X"
        getAllTaskStatesByProjectIdUI.run()
        assertThat(printed.last()).contains("Cancelled.")
        verify(exactly = 0) { getAllTaskStatesByProjectIdUseCase(any()) }
    }

    @Test
    fun `invalid selection shows error`() {
        every { reader.read() } returns "99"
        getAllTaskStatesByProjectIdUI.run()
        assertThat(printed.last()).contains("Invalid selection")
        verify(exactly = 0) { getAllTaskStatesByProjectIdUseCase(any()) }
    }




    private companion object {
        val projectP1 = Project("P1", "Core", null, emptyList(), emptyList())
        val stateTodo = TaskState("S1", "TODO", "P1")
        val stateDone = TaskState("S2", "DONE", "P1")
        const val projectIdWithNoStates = "P1"
    }
}
