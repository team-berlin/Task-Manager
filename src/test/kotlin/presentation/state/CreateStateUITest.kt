package com.berlin.presentation.state

import com.berlin.domain.model.Project
import com.berlin.domain.usecase.project.GetAllProjectsUseCase
import com.berlin.domain.usecase.state.CreateStateUseCase
import com.berlin.presentation.helper.choose
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import com.berlin.presentation.state.CreateStateUI
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CreateStateUITest {
    private lateinit var createStateUseCase: CreateStateUseCase
    private lateinit var createStateUi: CreateStateUI
    private lateinit var getAllProjectUseCase: GetAllProjectsUseCase
    private val viewer: Viewer = mockk(relaxed = true)
    private val reader: Reader = mockk(relaxed = true)


    private val testProject = Project(
        id = "P1",
        name = "Berlin Core",
        description = "The back-end",
        statesId = listOf("S1", "S2", "S3", "S4"),
        tasksId = mutableListOf()
    )

    @BeforeEach
    fun setup() {
        createStateUseCase = mockk(relaxed = true)
        getAllProjectUseCase = mockk(relaxed = true)
        createStateUi = CreateStateUI(createStateUseCase, getAllProjectUseCase,viewer, reader)

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
        createStateUi.run()

        // Then
        verify { viewer.show("State Name can not be empty") }
        verify(exactly = 3) { reader.read() }
    }

    @Test
    fun `should exit state creation when user enters exit command`() {
        // Given
        every { reader.read() } returns "exit"

        // When
        createStateUi.run()

        // Then
        verify(exactly = 1) { reader.read() }
    }

    @Test
    fun `should create state successfully when valid state name is provided`() {
        // Given
        val stateName = "NewState"

        every { reader.read() } returnsMany listOf(stateName, "exit")
        every {
            createStateUseCase.createNewState(stateName, testProject.id)
        } returns Result.success("State created successfully")

        // When
        createStateUi.run()

        // Then
        verify { createStateUseCase.createNewState(stateName, testProject.id) }
        verify { viewer.show("State created successfully") }
    }

    @Test
    fun `should display error message when state creation fails`() {
        // Given
        val stateName = "InvalidState"

        every { reader.read() } returnsMany listOf(stateName, "exit")
        every {
            createStateUseCase.createNewState(stateName, testProject.id)
        } returns Result.failure(Exception("Creation Failed"))

        // When
        createStateUi.run()

        // Then
        verify { createStateUseCase.createNewState(stateName, testProject.id) }
        verify { viewer.show("Creation Failed") }
    }

    @Test
    fun `should handle case insensitive exit command`() {
        // Given
        every { reader.read() } returns "ExIt"

        // When
        createStateUi.run()

        // Then
        verify(exactly = 1) { reader.read() }
    }
}
