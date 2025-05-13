package com.berlin.presentation.task_state

import com.berlin.domain.model.Project
import com.berlin.domain.usecase.project.GetAllProjectsUseCase
import com.berlin.domain.usecase.task_state.CreateTaskStateUseCase
import com.berlin.presentation.helper.choose
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CreateTaskStateUITest {
    private lateinit var createTaskStateUseCase: CreateTaskStateUseCase
    private lateinit var createTaskStateUi: CreateTaskStateUI
    private lateinit var getAllProjectUseCase: GetAllProjectsUseCase
    private val viewer: Viewer = mockk(relaxed = true)
    private val reader: Reader = mockk(relaxed = true)


    private val testProject = Project(
        id = "P1",
        title = "Berlin Core",
        description = "The back-end",
        statesId = listOf("S1", "S2", "S3", "S4"),
        tasksId = mutableListOf()
    )

    @BeforeEach
    fun setup() {
        createTaskStateUseCase = mockk(relaxed = true)
        getAllProjectUseCase = mockk(relaxed = true)
        createTaskStateUi = CreateTaskStateUI(createTaskStateUseCase, getAllProjectUseCase,viewer, reader)

        // Mock the choose function to return our test project
        mockkStatic("com.berlin.presentation.helper.ChooserKt")
        every {
            choose<Project>(
                any(), any(), any(), viewer, reader
            )
        } returns testProject
    }

    @Test
    fun `should display error message when state name is null or empty`() {
        // Given
        every { reader.read() } returnsMany listOf("", null, "exit")

        // When
        createTaskStateUi.run()

        // Then
        verify { viewer.show("State Name can not be empty") }
        verify(exactly = 3) { reader.read() }
    }

    @Test
    fun `should exit state creation when user enters exit command`() {
        // Given
        every { reader.read() } returns "exit"

        // When
        createTaskStateUi.run()

        // Then
        verify(exactly = 1) { reader.read() }
    }

    @Test
    fun `should create state successfully when valid state name is provided`() {
        // Given
        val stateName = "NewState"

        every { reader.read() } returnsMany listOf(stateName, "exit")
        every {
            createTaskStateUseCase(stateName, testProject.id)
        } returns "State created successfully"

        // When
        createTaskStateUi.run()

        // Then
        verify { createTaskStateUseCase(stateName, testProject.id) }
        verify { viewer.show("State created successfully") }
    }

    @Test
    fun `should return to main menu when exit`() {
        // Given
        val stateName = "InvalidState"

        every { reader.read() } returnsMany listOf(stateName, "exit")

        // When
        createTaskStateUi.run()

        // Then
        verify { createTaskStateUseCase(stateName, testProject.id) }
    }

    @Test
    fun `should handle case insensitive exit command`() {
        // Given
        every { reader.read() } returns "ExIt"

        // When
        createTaskStateUi.run()

        // Then
        verify(exactly = 1) { reader.read() }
    }
}
