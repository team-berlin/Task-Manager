package com.berlin.presentation.state

import com.berlin.data.DummyData
import com.berlin.domain.model.Project
import com.berlin.domain.model.TaskState
import com.berlin.domain.usecase.project.GetAllProjectsUseCase
import com.berlin.domain.usecase.state.GetAllStatesByProjectIdUseCase
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetAllStatesByProjectIdUITest {

    private val printed = mutableListOf<String>()
    private val viewer: Viewer = mockk(relaxed = true) {
        every { show(capture(printed)) } just Runs
    }
    private val reader: Reader = mockk()
    private lateinit var getAllStatesByProjectIdUseCase: GetAllStatesByProjectIdUseCase
    private lateinit var getAllProjectsUseCase: GetAllProjectsUseCase
    private lateinit var getAllStatesByProjectIdUI: GetAllStatesByProjectIdUI

    @BeforeEach
    fun setUp() {
        getAllProjectsUseCase = mockk()
        every { getAllProjectsUseCase() } returns DummyData.projects

        DummyData.projects.clear()
        DummyData.states.clear()
        printed.clear()

        DummyData.projects += projectP1
        DummyData.states += listOf(stateTodo, stateDone)

        getAllStatesByProjectIdUseCase = mockk()
        getAllStatesByProjectIdUI = GetAllStatesByProjectIdUI(getAllStatesByProjectIdUseCase, getAllProjectsUseCase, viewer, reader)
    }

    @Test
    fun `shows swimlane with states`() {
        every { reader.read() } returns "1"
        every { getAllStatesByProjectIdUseCase(projectIdWithNoStates) } returns
                listOf(
                    stateTodo,
                    stateDone
                )

        getAllStatesByProjectIdUI.run()
        assertThat(printed).contains("\n=== States for project P1 ===")
        assertThat(printed).contains("- S1: TODO")
        assertThat(printed).contains("- S2: DONE")
    }

    @Test
    fun `shows (no states) when project has no states`() {
        every { reader.read() } returns "1"
        every { getAllStatesByProjectIdUseCase(projectIdWithNoStates) } returns emptyList()
        getAllStatesByProjectIdUI.run()
        assertThat(printed).contains("  (no states)")
    }

    @Test
    fun `cancelling input shows Cancelled`() {
        every { reader.read() } returns "X"
        getAllStatesByProjectIdUI.run()
        assertThat(printed.last()).contains("Cancelled.")
        verify(exactly = 0) { getAllStatesByProjectIdUseCase(any()) }
    }

    @Test
    fun `invalid selection shows error`() {
        every { reader.read() } returns "99"
        getAllStatesByProjectIdUI.run()
        assertThat(printed.last()).contains("Invalid selection")
        verify(exactly = 0) { getAllStatesByProjectIdUseCase(any()) }
    }




    private companion object {
        val projectP1 = Project("P1", "Core", null, emptyList(), emptyList())
        val stateTodo = TaskState("S1", "TODO", "P1")
        val stateDone = TaskState("S2", "DONE", "P1")
        const val projectIdWithNoStates = "P1"
    }
}
