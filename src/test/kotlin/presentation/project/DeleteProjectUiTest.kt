package com.berlin.presentation.project

import com.berlin.domain.exception.InvalidProjectException
import com.berlin.domain.exception.InvalidProjectIdException
import com.berlin.domain.model.Project
import com.berlin.domain.usecase.project.DeleteProjectUseCase
import com.berlin.domain.usecase.project.GetAllProjectsUseCase

import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifySequence
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test


class DeleteProjectUiTest {

    private lateinit var deleteProjectUseCase: DeleteProjectUseCase
    private lateinit var getAllProjectsUseCase: GetAllProjectsUseCase
    private lateinit var viewer: Viewer
    private lateinit var reader: Reader
    private lateinit var ui: DeleteProjectUi

    private val project = Project("prj-123", "Test Project", null, null, null)

    @BeforeEach
    fun setup() {
        deleteProjectUseCase = mockk()
        getAllProjectsUseCase = mockk()
        viewer = mockk(relaxed = true)
        reader = mockk()
        ui = DeleteProjectUi(deleteProjectUseCase, getAllProjectsUseCase, viewer, reader)
    }

    @Test
    fun `run should delete project successfully when confirmed`() {
        // Given
        every { getAllProjectsUseCase() } returns listOf(project)
        every { reader.read() } returns "1" andThen "y"
        every { deleteProjectUseCase(project.id) } returns "Deleted Successfully"

        // When
        ui.run()

        // Then
        verifySequence {
            viewer.show("--- Projects ---")
            viewer.show("1. ${project.id} – ${project.title}")
            viewer.show("X – Cancel\nSelect:")
            reader.read()
            viewer.show("Type Y to confirm deletion:")
            reader.read()
            deleteProjectUseCase(project.id)
            viewer.show("${project.title} is Deleted.")
        }
    }

    @Test
    fun `run should cancel when user types X`() {
        // Given
        every { getAllProjectsUseCase() } returns listOf(project)
        every { reader.read() } returns "x"

        // When
        ui.run()

        // Then
        verify {
            viewer.show("Cancelled.")
        }
    }

    @Test
    fun `run should cancel when user does not confirm deletion`() {
        // Given
        every { getAllProjectsUseCase() } returns listOf(project)
        every { reader.read() } returns "1" andThen "no"

        // When
        ui.run()

        // Then
        verify {
            viewer.show("Type Y to confirm deletion:")
            viewer.show("Cancelled.")
        }
    }

    @Test
    fun `run should show invalid selection when input is not a number`() {
        // Given
        every { getAllProjectsUseCase() } returns listOf(project)
        every { reader.read() } returns "aaa"

        // When
        ui.run()

        // Then
        verify {
            viewer.show("Invalid selection")
        }
    }

    @Test
    fun `run should show invalid selection when input is out of range`() {
        // Given
        every { getAllProjectsUseCase() } returns listOf(project)
        every { reader.read() } returns "5"

        // When
        ui.run()

        // Then
        verify {
            viewer.show("Invalid selection")
        }
    }

    @Test
    fun `run should show deletion failure message when delete fails`() {
        // Given
        val failureMessage = "delete is failed"
        every { getAllProjectsUseCase() } returns listOf(project)
        every { reader.read() } returns "1" andThen "y"
        every { deleteProjectUseCase(project.id) } throws InvalidProjectException("")

        // When
        ui.run()

        // Then
        verify {
            viewer.show(failureMessage)
        }
    }

    @Test
    fun `run should show invalid project id when deleteProject throws`() {
        // Given
        every { getAllProjectsUseCase() } returns listOf(project)
        every { reader.read() } returns "1" andThen "y"
        every { deleteProjectUseCase(project.id) } throws InvalidProjectIdException("Error")

        // When
        ui.run()

        // Then
        verify {
            viewer.show("invalid project id")
        }
    }
}

