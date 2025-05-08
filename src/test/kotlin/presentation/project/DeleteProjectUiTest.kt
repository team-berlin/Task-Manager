package presentation.project

import com.berlin.domain.exception.InvalidProjectIdException
import com.berlin.domain.model.Project
import com.berlin.domain.usecase.project.DeleteProjectUseCase
import com.berlin.domain.usecase.project.GetAllProjectsUseCase

import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import com.berlin.presentation.project.DeleteProjectUi
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifySequence
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.Test


class DeleteProjectUiTest {

    private lateinit var deleteProject: DeleteProjectUseCase
    private lateinit var getAllProjects: GetAllProjectsUseCase
    private lateinit var viewer: Viewer
    private lateinit var reader: Reader
    private lateinit var ui: DeleteProjectUi

    private val project = Project("prj-123", "Test Project", null, null, null)

    @BeforeEach
    fun setup() {
        deleteProject = mockk()
        getAllProjects = mockk()
        viewer = mockk(relaxed = true)
        reader = mockk()
        ui = DeleteProjectUi(deleteProject, getAllProjects, viewer, reader)
    }

    @Test
    fun `run should delete project successfully when confirmed`() {
        // Given
        every { getAllProjects.getAllProjects() } returns listOf(project)
        every { reader.read() } returns "1" andThen "y"
        every { deleteProject.deleteProject(project.id) } returns Result.success("Deleted Successfully")

        // When
        ui.run()

        // Then
        verifySequence {
            viewer.show("--- Projects ---")
            viewer.show("1. ${project.id} – ${project.name}")
            viewer.show("X – Cancel\nSelect:")
            reader.read()
            viewer.show("Type Y to confirm deletion:")
            reader.read()
            deleteProject.deleteProject(project.id)
            viewer.show("Deleted.")
        }
    }

    @Test
    fun `run should cancel when user types X`() {
        // Given
        every { getAllProjects.getAllProjects() } returns listOf(project)
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
        every { getAllProjects.getAllProjects() } returns listOf(project)
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
        every { getAllProjects.getAllProjects() } returns listOf(project)
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
        every { getAllProjects.getAllProjects() } returns listOf(project)
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
        every { getAllProjects.getAllProjects() } returns listOf(project)
        every { reader.read() } returns "1" andThen "y"
        every { deleteProject.deleteProject(project.id) } returns Result.failure(Exception(failureMessage))

        // When
        ui.run()

        // Then
        verify {
            viewer.show(failureMessage)
        }
    }

    @Test
    fun `run should show default failure message when exception has no message`() {
        // Given
        every { getAllProjects.getAllProjects() } returns listOf(project)
        every { reader.read() } returns "1" andThen "y"
        every { deleteProject.deleteProject(project.id) } returns Result.failure(Exception())

        // When
        ui.run()

        // Then
        verify {
            viewer.show("Deletion failed")
        }
    }


    @Test
    fun `run should show invalid project id when deleteProject throws`() {
        // Given
        every { getAllProjects.getAllProjects() } returns listOf(project)
        every { reader.read() } returns "1" andThen "y"
        every { deleteProject.deleteProject(project.id) } throws InvalidProjectIdException("Error")

        // When
        ui.run()

        // Then
        verify {
            viewer.show("invalid project id")
        }
    }
}

