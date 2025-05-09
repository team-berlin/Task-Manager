package com.berlin.presentation.state

import com.berlin.data.DummyData
import com.berlin.domain.exception.InvalidProjectIdException
import com.berlin.domain.model.Project
import com.berlin.domain.model.State
import com.berlin.domain.usecase.project.GetAllProjectsUseCase
import com.berlin.domain.usecase.state.GetAllStatesByProjectIdUseCase
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetAllStatesByProjectIdUiTest {

    private val printed = mutableListOf<String>()
    private val viewer: Viewer = mockk(relaxed = true) {
        every { show(capture(printed)) } just Runs
    }
    private val reader: Reader = mockk()
    private lateinit var getAllProjects: GetAllProjectsUseCase
    private lateinit var useCase: GetAllStatesByProjectIdUseCase
    private lateinit var ui: GetAllStatesByProjectIdUi


    @BeforeEach
    fun setUp() {
        DummyData.projects.clear()
        DummyData.states.clear()
        printed.clear()

        DummyData.projects += projectP1
        DummyData.states += listOf(stateTodo, stateDone)
        getAllProjects= mockk()
        every { getAllProjects.getAllProjects() }returns DummyData.projects
        useCase = mockk()
        ui = GetAllStatesByProjectIdUi(useCase,getAllProjects ,viewer, reader)
    }

    @Test
    fun `shows swimlane with states`() {
        //Given
        every { reader.read() } returns "1"
        every { useCase.getAllStatesByProjectId(projectIdWithNoStates) } returns Result.success(
            listOf(
                stateTodo,
                stateDone
            )
        )

        //Given
        ui.run()

        //Then
        assertThat(printed).contains("\n=== States for project P1 ===")
        assertThat(printed).contains("- S1: TODO")
        assertThat(printed).contains("- S2: DONE")
    }

    @Test
    fun `shows (no states) when project has no states`() {
        //Given

        every { reader.read() } returns "1"
        every { useCase.getAllStatesByProjectId(projectIdWithNoStates) } returns Result.success(emptyList())

        //When
        ui.run()

        //Then
        assertThat(printed).contains("  (no states)")
    }

    @Test
    fun `cancelling input shows Cancelled`() {
        //Given
        every { reader.read() } returns "X"

        //When
        ui.run()

        //Then
        assertThat(printed.last()).contains("Cancelled.")
        verify(exactly = 0) { useCase.getAllStatesByProjectId(any()) }
    }

    @Test
    fun `invalid selection shows error`() {
        //Given
        every { reader.read() } returns "99"

        //When
        ui.run()

        //Then
        assertThat(printed.last()).contains("Invalid selection")
        verify(exactly = 0) { useCase.getAllStatesByProjectId(any()) }
    }

    @Test
    fun `getAllStatesByProjectId shows message Failed to load when failure`() {
        //Given
        every { reader.read() } returns "1"
        every { useCase.getAllStatesByProjectId(projectIdWithNoStates) } returns Result.failure(RuntimeException("Failed to load"))

        //When
        ui.run()

        //Then
        assertThat(printed.last()).contains("Failed to load")
        verify(exactly = 1) { useCase.getAllStatesByProjectId("P1") }
    }

    //TODO review exception thrown
    @Test
    fun `throws InvalidProjectIdException and shows invalid project id`() {
        //Given
        every { reader.read() } returns "1"
        every { useCase.getAllStatesByProjectId(projectIdWithNoStates) } throws InvalidProjectIdException("invalid project id")

        //When
        ui.run()

        //Then
        assertThat(printed.last()).contains("invalid project id")
        verify(exactly = 1) { useCase.getAllStatesByProjectId(projectIdWithNoStates) }
    }

    private companion object {
        val projectP1 = Project("P1", "Core", null, emptyList(), emptyList())
        val stateTodo = State("S1", "TODO", "P1")
        val stateDone = State("S2", "DONE", "P1")
        const val projectIdWithNoStates = "P1"
    }
}
