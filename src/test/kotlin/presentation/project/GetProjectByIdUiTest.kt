package com.berlin.presentation.project

import com.berlin.domain.exception.InvalidProjectIdException
import com.berlin.domain.exception.ProjectNotFoundException
import com.berlin.domain.model.Project
import com.berlin.domain.usecase.project.GetProjectByIdUseCase
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifySequence
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class GetProjectByIdUiTest {

    private lateinit var getProjectByIdUseCase: GetProjectByIdUseCase
    private lateinit var viewer: Viewer
    private lateinit var reader: Reader
    private lateinit var getProjectByIdUi: GetProjectByIdUi

    private val project = Project(
        id = "proj-1",
        title = "Test Project",
        description = "Sample description",
        statesId = listOf("S1", "S2"),
        tasksId = listOf("T1", "T2")
    )

    private val project2 = Project(
        id = "proj-2",
        title = "Test Project",
        description = "Sample description",
        statesId = emptyList(),
        tasksId = emptyList()
    )

    private val project3 = Project(
        id = "proj-3",
        title = "Test Project",
        description = null,
        statesId = listOf("S1", "S2"),
        tasksId = listOf("T1", "T2")
    )

    private val project4 = Project(
        id = "proj-4",
        title = "Test Project",
        description = "Sample description",
        statesId = listOf("S1", "S2"),
        tasksId = null
    )

    private val project5 = Project(
        id = "proj-5",
        title = "Test Project",
        description = "Sample description",
        statesId = null,
        tasksId = listOf("T1", "T2")
    )

    @BeforeEach
    fun setup() {
        getProjectByIdUseCase = mockk()
        viewer = mockk(relaxed = true)
        reader = mockk()
        getProjectByIdUi = GetProjectByIdUi(getProjectByIdUseCase, viewer, reader)
    }

    @Test
    fun `run shows project details with states and tasks`() {
        //Given
        every { reader.read() } returns project.id
        every { getProjectByIdUseCase(project.id) } returns project

        //When
        getProjectByIdUi.run()

        //Then
        verifySequence {
            viewer.show("Enter project ID:")
            reader.read()
            viewer.show("ID: ${project.id}")
            viewer.show("Title: ${project.title}")
            viewer.show("Description: ${project.description}")
            viewer.show("States:")
            viewer.show(" - [S1] S1")
            viewer.show(" - [S2] S2")
            viewer.show("\nTasks:")
            viewer.show(" - Task ID: T1")
            viewer.show(" - Task ID: T2")
        }
    }

    @Test
    fun `run shows no states or tasks when both are empty`() {

        //Given
        every { reader.read() } returns project2.id
        every { getProjectByIdUseCase(project2.id) } returns project2

        //When
        getProjectByIdUi.run()

        //Then
        verify {
            viewer.show("No states defined for this project.")
            viewer.show("\nNo tasks defined for this project.")
        }
    }

    @Test
    fun `run shows invalid project ID when InvalidProjectIdException is thrown`() {

        //Given
        every { reader.read() } returns "no-id"
        every { getProjectByIdUseCase("no-id") } throws InvalidProjectIdException("Invalid project ID")

        //When
        getProjectByIdUi.run()

        //Then
        verify { viewer.show("Invalid project ID") }
    }

    @Test
    fun `run shows no project found when ProjectNotFoundException is thrown`() {

        //Given
        every { reader.read() } returns "proj-999"
        every { getProjectByIdUseCase("proj-999") } throws ProjectNotFoundException("No project found with ID")

        //When
        getProjectByIdUi.run()

        //Then
        verify { viewer.show("No project found with ID") }
    }

    @Test
    fun `run shows message when unknown exception is thrown`() {

        //Given
        every { reader.read() } returns "proj-x"
        every { getProjectByIdUseCase("proj-x") } throws Exception("Something went wrong")

        //When
        getProjectByIdUi.run()

        //Then
        verify { viewer.show("Something went wrong") }
    }

    @Test
    fun `run shows default error message when exception has no message`() {

        //Given
        every { reader.read() } returns "proj-null"
        every { getProjectByIdUseCase("proj-null") } throws Exception()

        //When
        getProjectByIdUi.run()

        //Then
        verify { viewer.show("Lookup failed") }
    }

    @Test
    fun `run handles null input from reader and shows invalid ID`() {

        //Given
        every { reader.read() } returns null
        every { getProjectByIdUseCase("") } throws InvalidProjectIdException("Invalid project ID")

        //When
        getProjectByIdUi.run()

        //Then
        verify { viewer.show("Invalid project ID") }
    }

    @Test
    fun `run trims input from reader and passes to use case`() {

        //Given
        every { reader.read() } returns "   proj-1  "
        every { getProjectByIdUseCase("proj-1") } returns project

        //When
        getProjectByIdUi.run()

        //Then
        verify { getProjectByIdUseCase("proj-1") }
    }

    @Test
    fun `run shows (none) when project has no description`() {

        //Given
        every { reader.read() } returns project3.id
        every { getProjectByIdUseCase(project3.id) } returns project3

        //When
        getProjectByIdUi.run()

        //Then
        verify { viewer.show("Description: (none)") }
    }

    @Test
    fun `run handles null statesId and shows no states`() {

        //Given
        every { reader.read() } returns project5.id
        every { getProjectByIdUseCase(project5.id) } returns project5

        //When
        getProjectByIdUi.run()

        //Then
        verify { viewer.show("No states defined for this project.") }
    }

    @Test
    fun `run handles null tasksId and shows no tasks`() {

        //Given
        every { reader.read() } returns project4.id
        every { getProjectByIdUseCase(project4.id) } returns project4

        //When
        getProjectByIdUi.run()

        //Then
        verify { viewer.show("\nNo tasks defined for this project.") }
    }
}




