package com.berlin.presentation.project

import com.berlin.domain.exception.InvalidProjectException
import com.berlin.domain.usecase.project.CreateProjectUseCase
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifySequence
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class CreateProjectUiTest {

    private lateinit var createProjectUseCase: CreateProjectUseCase
    private lateinit var viewer: Viewer
    private lateinit var reader: Reader
    private lateinit var ui: CreateProjectUi

    @BeforeEach
    fun setup() {
        createProjectUseCase = mockk(relaxed = true)
        viewer = mockk(relaxed = true)
        reader = mockk(relaxed = true)
        ui = CreateProjectUi(createProjectUseCase, viewer, reader)
    }

    @Test
    fun `run should create project and show success message`() {
        // Given
        val name = "TestProject"
        val description = "Test Description"

        every { reader.read() } returns name andThen "yes" andThen description
        every {
            createProjectUseCase(
                name, description, null, null
            )
        } returns "Creation Successfully"

        // When
        ui.run()

        // Then
        verify { viewer.show("Creation Successfully") }
    }

    @Test
    fun `run should show invalid project details when project creation fails`() {
        // Given
        val name = "ValidProject"
        val description = "Test Description"

        every { reader.read() } returns name andThen "yes" andThen description
        every { createProjectUseCase(name, description, null, null) } throws InvalidProjectException("")

        // When
        ui.run()

        // Then
        verifySequence {
            viewer.show("=== Create New Project ===\n")
            viewer.show("================================================================\n\n")
            viewer.show("Enter project details:\n")
            viewer.show("Project Title:")
            reader.read()
            viewer.show("Do you want to write a description? (yes/no)")
            reader.read()
            viewer.show("Enter project description:")
            reader.read()
            viewer.show("Creating project...\n")
            createProjectUseCase(
                name, description, null, null
            )
            viewer.show("invalid project details")
        }
    }

    @Test
    fun `run should view to user again when input null project name`() {
        //given
        every { reader.read() } returns null andThen "ggg" andThen "sfd"
        every { createProjectUseCase(any(), any(), any(), any()) } returns "Creation Successfully"
        //when
        ui.run()
        //then
        verify { viewer.show("Please enter a valid project name:") }
    }

    @Test
    fun `run should view to user again when input blank project name`() {
        //given
        every { reader.read() } returns "" andThen "ggg" andThen "sfd"
        every { createProjectUseCase(any(), any(), any(), any()) } returns "Creation Successfully"
        //when
        ui.run()
        //then
        verify { viewer.show("Please enter a valid project name:") }
    }

    @Test
    fun `run should create project when description is null`() {
        // Given
        val name = "MyProject"

        every { reader.read() } returns name andThen "no"
        every {
            createProjectUseCase(
                name, null, null, null
            )
        } returns "Creation Successfully"

        // When
        ui.run()

        // Then
        verify { viewer.show("Creation Successfully") }
    }
}