package com.berlin.presentation.project

import com.berlin.domain.model.Project
import com.berlin.domain.usecase.project.GetAllProjectsUseCase
import com.berlin.domain.usecase.project.GetProjectByIdUseCase
import com.berlin.domain.usecase.project.UpdateProjectUseCase
import com.berlin.helper.projectHelper
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals

class UpdateProjectUiTest {
    private lateinit var updateProjectUseCase: UpdateProjectUseCase
    private lateinit var getAllProjectsUseCase: GetAllProjectsUseCase
    private lateinit var getProjectByIdUseCase: GetProjectByIdUseCase
    private lateinit var updateProjectUi: UpdateProjectUi
    private val viewer: Viewer = mockk(relaxed = true)
    private val reader: Reader = mockk(relaxed = true)

    @BeforeEach
    fun setup() {
        updateProjectUseCase = mockk(relaxed = true)
        getAllProjectsUseCase = mockk(relaxed = true)
        getProjectByIdUseCase = mockk(relaxed = true)
        updateProjectUi =
            UpdateProjectUi(updateProjectUseCase, getAllProjectsUseCase, getProjectByIdUseCase, viewer, reader)
    }


    @Test
    fun `run should update project title successfully`() {
        // Given
        val projects = listOf(
            projectHelper(id = "project-1", name = "Project 1"),
            projectHelper(id = "project-2", name = "Project 2")
        )
        val projectId = "project-1"
        val project = projects[0]
        val newTitle = "Updated Project 1"

        every { getAllProjectsUseCase.getAllProjects() } returns projects
        every { reader.read() } returnsMany listOf(projectId, "1", newTitle, "no")
        every { getProjectByIdUseCase.getProjectById(projectId) } returns project
        every { updateProjectUseCase.updateProject(any()) } returns Result.success("Updated Successfully")

        // When
        updateProjectUi.run()

        // Then
        verify { getAllProjectsUseCase.getAllProjects() }
        verify { getProjectByIdUseCase.getProjectById(projectId) }
        val projectSlot = slot<Project>()
        verify { updateProjectUseCase.updateProject(capture(projectSlot)) }
        assertEquals(newTitle, projectSlot.captured.title)
        verify { viewer.show(match { it.contains("Project updated successfully") }) }
    }

    @Test
    fun `run should update project description successfully`() {
        // Given
        val projects = listOf(
            projectHelper(id = "project-1", name = "Project 1", description = "Description 1"),
            projectHelper(id = "project-2", name = "Project 2", description = "Description 2")
        )
        val projectId = "project-1"
        val project = projects[0]
        val newDescription = "This is an updated description"

        every { getAllProjectsUseCase.getAllProjects() } returns projects
        every { reader.read() } returnsMany listOf(projectId, "2", newDescription, "no")
        every { getProjectByIdUseCase.getProjectById(projectId) } returns project
        every { updateProjectUseCase.updateProject(any()) } returns Result.success("Updated Successfully")

        // When
        updateProjectUi.run()

        // Then
        verify { getAllProjectsUseCase.getAllProjects() }
        verify { getProjectByIdUseCase.getProjectById(projectId) }
        val projectSlot = slot<Project>()
        verify { updateProjectUseCase.updateProject(capture(projectSlot)) }
        assertEquals(newDescription, projectSlot.captured.description)
        verify { viewer.show(match { it.contains("Project updated successfully") }) }
    }

    @Test
    fun `run should update multiple fields when user selects yes to continue`() {
        // Given
        val projects = listOf(
            projectHelper(id = "project-1", name = "Project 1", description = "Description 1")
        )
        val projectId = "project-1"
        val project = projects[0]
        val newTitle = "Updated Project 1"
        val newDescription = "This is an updated description"

        every { getAllProjectsUseCase.getAllProjects() } returns projects
        every { reader.read() } returnsMany listOf(
            projectId, "1", newTitle, "yes", "2", newDescription, "no"
        )
        every { getProjectByIdUseCase.getProjectById(projectId) } returns project
        every { updateProjectUseCase.updateProject(any()) } returns Result.success("Updated Successfully")

        // When
        updateProjectUi.run()

        // Then
        verify { getAllProjectsUseCase.getAllProjects() }
        verify { getProjectByIdUseCase.getProjectById(projectId) }
        val projectSlot = slot<Project>()
        verify { updateProjectUseCase.updateProject(capture(projectSlot)) }
        assertEquals(newTitle, projectSlot.captured.title)
        assertEquals(newDescription, projectSlot.captured.description)
        verify { viewer.show(match { it.contains("Project updated successfully") }) }
    }

    @Test
    fun `run should display failure message when update fails`() {
        // Given
        val projects = listOf(
            projectHelper(id = "project-1", name = "Project 1", description = "Description 1")
        )
        val projectId = "project-1"
        val project = projects[0]
        val newTitle = "Updated Project 1"
        val errorMessage = "Update failed"

        every { getAllProjectsUseCase.getAllProjects() } returns projects
        every { reader.read() } returnsMany listOf(projectId, "1", newTitle, "no")
        every { getProjectByIdUseCase.getProjectById(projectId) } returns project
        every { updateProjectUseCase.updateProject(any()) } returns Result.failure(Exception(errorMessage))

        // When
        updateProjectUi.run()

        // Then
        verify { getAllProjectsUseCase.getAllProjects() }
        verify { getProjectByIdUseCase.getProjectById(projectId) }
        verify { updateProjectUseCase.updateProject(any()) }
        verify { viewer.show(match { it.contains("Project update failed") }) }
    }


    @Test
    fun `run should handle empty title input by retrying another input`() {
        // Given
        val projects = listOf(
            projectHelper(id = "project-1", name = "Project 1", description = "Description 1")
        )
        val projectId = "project-1"
        val project = projects[0]
        val emptyTitle = ""
        val validTitle = "Valid Title"

        every { getAllProjectsUseCase.getAllProjects() } returns projects
        every { reader.read() } returnsMany listOf(projectId, "1", emptyTitle, validTitle, "no")
        every { getProjectByIdUseCase.getProjectById(projectId) } returns project
        every { updateProjectUseCase.updateProject(any()) } returns Result.success("Updated Successfully")

        // When
        updateProjectUi.run()

        // Then
        verify { getAllProjectsUseCase.getAllProjects() }
        verify { getProjectByIdUseCase.getProjectById(projectId) }
        val projectSlot = slot<Project>()
        verify { updateProjectUseCase.updateProject(capture(projectSlot)) }
        assertEquals(validTitle, projectSlot.captured.title)
        verify { viewer.show(match { it.contains("Project title cannot be empty") }) }
    }

    @Test
    fun `run should handle different capitalization of yes and no`() {
        // Given
        val projects = listOf(
            projectHelper(id = "project-1", name = "Project 1", description = "Description 1")
        )
        val projectId = "project-1"
        val project = projects[0]
        val newTitle = "Updated Title"
        val newDescription = "Updated Description"

        every { getAllProjectsUseCase.getAllProjects() } returns projects
        every { reader.read() } returnsMany listOf(
            projectId,
            "1", newTitle, "YeS",
            "2", newDescription, "nO"
        )
        every { getProjectByIdUseCase.getProjectById(projectId) } returns project
        every { updateProjectUseCase.updateProject(any()) } returns Result.success("Updated Successfully")

        // When
        updateProjectUi.run()

        // Then
        val projectSlot = slot<Project>()
        verify { updateProjectUseCase.updateProject(capture(projectSlot)) }
        assertEquals(newTitle, projectSlot.captured.title)
        assertEquals(newDescription, projectSlot.captured.description)
    }

    @Test
    fun `run should handle invalid continuation option gracefully`() {
        // Given
        val projects = listOf(
            projectHelper(id = "project-1", name = "Project 1", description = "Description 1")
        )
        val projectId = "project-1"
        val project = projects[0]
        val newTitle = "Updated Title"
        val invalidOption = "maybe"
        val validOption = "no"

        every { getAllProjectsUseCase.getAllProjects() } returns projects
        every { reader.read() } returnsMany listOf(
            projectId,
            "1", newTitle, invalidOption, validOption
        )
        every { getProjectByIdUseCase.getProjectById(projectId) } returns project
        every { updateProjectUseCase.updateProject(any()) } returns Result.success("Updated Successfully")

        // When
        updateProjectUi.run()

        // Then
        verify { getAllProjectsUseCase.getAllProjects() }
        verify { getProjectByIdUseCase.getProjectById(projectId) }
        verify { updateProjectUseCase.updateProject(any()) }
        verify { viewer.show(match { it.contains("Please enter 'yes' or 'no'") }) }
    }

    @Test
    fun `run should handle null description input`() {
        // Given
        val projects = listOf(
            projectHelper(id = "project-1", name = "Project 1", description = "Description 1")
        )
        val projectId = "project-1"
        val project = projects[0]

        every { getAllProjectsUseCase.getAllProjects() } returns projects
        every { reader.read() } returnsMany listOf(projectId, "2", null, "no")
        every { getProjectByIdUseCase.getProjectById(projectId) } returns project
        every { updateProjectUseCase.updateProject(any()) } returns Result.success("Updated Successfully")

        // When
        updateProjectUi.run()

        // Then
        verify { getAllProjectsUseCase.getAllProjects() }
        verify { getProjectByIdUseCase.getProjectById(projectId) }
        val projectSlot = slot<Project>()
        verify { updateProjectUseCase.updateProject(capture(projectSlot)) }
        assertEquals(null, projectSlot.captured.description)
    }

    @Test
    fun `run should exit update loop when user enters no`() {
        // Given
        val projects = listOf(
            projectHelper(id = "project-1", name = "Project 1", description = "Description 1")
        )
        val projectId = "project-1"
        val project = projects[0]
        val newTitle = "Updated Project 1"

        every { getAllProjectsUseCase.getAllProjects() } returns projects
        every { reader.read() } returnsMany listOf(projectId, "1", newTitle, "no")
        every { getProjectByIdUseCase.getProjectById(projectId) } returns project
        every { updateProjectUseCase.updateProject(any()) } returns Result.success("Updated Successfully")

        // When
        updateProjectUi.run()

        // Then
        verify(exactly = 1) { updateProjectUseCase.updateProject(any()) }
        verify { viewer.show("Project updated successfully!\n") }
    }

    @Test
    fun `run should continue update loop when user enters yes`() {
        // Given
        val projects = listOf(
            projectHelper(id = "project-1", name = "Project 1", description = "Description 1")
        )
        val projectId = "project-1"
        val project = projects[0]
        val newTitle = "Updated Project 1"
        val newDescription = "Updated Description"

        every { getAllProjectsUseCase.getAllProjects() } returns projects
        every { reader.read() } returnsMany listOf(
            projectId,
            "1", newTitle, "yes",
            "2", newDescription, "no"
        )
        every { getProjectByIdUseCase.getProjectById(projectId) } returns project
        every { updateProjectUseCase.updateProject(any()) } returns Result.success("Updated Successfully")

        // When
        updateProjectUi.run()

        // Then
        val projectSlot = slot<Project>()
        verify { updateProjectUseCase.updateProject(capture(projectSlot)) }
        assertEquals(newTitle, projectSlot.captured.title)
        assertEquals(newDescription, projectSlot.captured.description)
        verify(exactly = 1) { updateProjectUseCase.updateProject(any()) }
    }

    @Test
    fun `run should accept uppercase YES and NO options`() {
        // Given
        val projects = listOf(
            projectHelper(id = "project-1", name = "Project 1", description = "Description 1")
        )
        val projectId = "project-1"
        val project = projects[0]
        val newTitle = "Updated Title"
        val newDescription = "Updated Description"

        every { getAllProjectsUseCase.getAllProjects() } returns projects
        every { reader.read() } returnsMany listOf(
            projectId,
            "1", newTitle, "YES",
            "2", newDescription, "NO"
        )
        every { getProjectByIdUseCase.getProjectById(projectId) } returns project
        every { updateProjectUseCase.updateProject(any()) } returns Result.success("Updated Successfully")

        // When
        updateProjectUi.run()

        // Then
        val projectSlot = slot<Project>()
        verify { updateProjectUseCase.updateProject(capture(projectSlot)) }
        assertEquals(newTitle, projectSlot.captured.title)
        assertEquals(newDescription, projectSlot.captured.description)
    }

    @Test
    fun `run should accept mixed case yes and no options`() {
        // Given
        val projects = listOf(
            projectHelper(id = "project-1", name = "Project 1", description = "Description 1")
        )
        val projectId = "project-1"
        val project = projects[0]
        val newTitle = "Updated Title"
        val newDescription = "Updated Description"

        every { getAllProjectsUseCase.getAllProjects() } returns projects
        every { reader.read() } returnsMany listOf(
            projectId,
            "1", newTitle, "YeS",
            "2", newDescription, "nO"
        )
        every { getProjectByIdUseCase.getProjectById(projectId) } returns project
        every { updateProjectUseCase.updateProject(any()) } returns Result.success("Updated Successfully")

        // When
        updateProjectUi.run()

        // Then
        val projectSlot = slot<Project>()
        verify { updateProjectUseCase.updateProject(capture(projectSlot)) }
        assertEquals(newTitle, projectSlot.captured.title)
        assertEquals(newDescription, projectSlot.captured.description)
    }


    @Test
    fun `run should handle trying different choices to found valid choice`() {
        // Given
        val projects = listOf(
            projectHelper(id = "project-1", name = "Project 1")
        )
        val projectId = "project-1"
        val project = projects[0]
        val invalidChoice1 = "5"
        val invalidChoice2 = "abc"
        val validChoice = "1"
        val newTitle = "Updated Title"

        every { getAllProjectsUseCase.getAllProjects() } returns projects
        every { reader.read() } returnsMany listOf(
            projectId, invalidChoice1, invalidChoice2, validChoice, newTitle, "no"
        )
        every { getProjectByIdUseCase.getProjectById(projectId) } returns project
        every { updateProjectUseCase.updateProject(any()) } returns Result.success("Updated Successfully")

        // When
        updateProjectUi.run()

        // Then
        verify { viewer.show("Please enter 1 or 2: ") }
        verify { viewer.show("Please enter a number (1 or 2): ") }
        verify { updateProjectUseCase.updateProject(any()) }
    }

    @Test
    fun `run should handle trying different inputs when empty title is provided`() {
        // Given
        val projects = listOf(
            projectHelper(id = "project-1", name = "Project 1")
        )
        val projectId = "project-1"
        val project = projects[0]
        val emptyTitle1 = ""
        val emptyTitle2 = ""
        val validTitle = "Valid Title"

        every { getAllProjectsUseCase.getAllProjects() } returns projects
        every { reader.read() } returnsMany listOf(
            projectId, "1", emptyTitle1, emptyTitle2, validTitle, "no"
        )
        every { getProjectByIdUseCase.getProjectById(projectId) } returns project
        every { updateProjectUseCase.updateProject(any()) } returns Result.success("Updated Successfully")

        // When
        updateProjectUi.run()

        // Then
        verify(exactly = 2) { viewer.show("Project title cannot be empty. Please enter a valid title: ") }
        verify { updateProjectUseCase.updateProject(any()) }
    }

    @Test
    fun `run should handle trying different options for continuation option`() {
        // Given
        val projects = listOf(
            projectHelper(id = "project-1", name = "Project 1")
        )
        val projectId = "project-1"
        val project = projects[0]
        val newTitle = "Updated Title"
        val invalidOption1 = "maybe"
        val invalidOption2 = "continue"
        val validOption = "no"

        every { getAllProjectsUseCase.getAllProjects() } returns projects
        every { reader.read() } returnsMany listOf(
            projectId, "1", newTitle, invalidOption1, invalidOption2, validOption
        )
        every { getProjectByIdUseCase.getProjectById(projectId) } returns project
        every { updateProjectUseCase.updateProject(any()) } returns Result.success("Updated Successfully")

        // When
        updateProjectUi.run()

        // Then
        verify(exactly = 2) { viewer.show("Please enter 'yes' or 'no': ") }
        verify { updateProjectUseCase.updateProject(any()) }
    }

    @Test
    fun `run should handle update of title followed by description`() {
        // Given
        val projects = listOf(
            projectHelper(id = "project-1", name = "Old Title", description = "Old Description")
        )
        val projectId = "project-1"
        val project = projects[0]
        val newTitle = "New Title"
        val newDescription = "New Description"

        every { getAllProjectsUseCase.getAllProjects() } returns projects
        every { reader.read() } returnsMany listOf(
            projectId, "1", newTitle, "yes", "2", newDescription, "no"
        )
        every { getProjectByIdUseCase.getProjectById(projectId) } returns project
        every { updateProjectUseCase.updateProject(any()) } returns Result.success("Updated Successfully")

        // When
        updateProjectUi.run()

        // Then
        val projectSlot = slot<Project>()
        verify { updateProjectUseCase.updateProject(capture(projectSlot)) }
        assertEquals(newTitle, projectSlot.captured.title)
        assertEquals(newDescription, projectSlot.captured.description)
    }

    @Test
    fun `promptForUpdateChoice should reject blank input`() {
        // Given
        val projects = listOf(projectHelper(id = "project-1", name = "Project 1"))
        val blankInput = ""
        val validChoice = "1"
        val newTitle = "New Title"

        every { getAllProjectsUseCase.getAllProjects() } returns projects
        every { reader.read() } returnsMany listOf("project-1", blankInput, validChoice, newTitle, "no")
        every { getProjectByIdUseCase.getProjectById("project-1") } returns projects[0]
        every { updateProjectUseCase.updateProject(any()) } returns Result.success("Success")

        // When
        updateProjectUi.run()

        // Then
        verify { viewer.show("Invalid choice. Please enter 1 or 2: ") }
    }

    @Test
    fun `updateProjectTitle should reject blank title`() {
        // Given
        val projects = listOf(projectHelper(id = "project-1", name = "Project 1"))
        val blankTitle = "   "
        val validTitle = "Valid Title"

        every { getAllProjectsUseCase.getAllProjects() } returns projects
        every { reader.read() } returnsMany listOf("project-1", "1", blankTitle, validTitle, "no")
        every { getProjectByIdUseCase.getProjectById("project-1") } returns projects[0]
        every { updateProjectUseCase.updateProject(any()) } returns Result.success("Success")

        // When
        updateProjectUi.run()

        // Then
        verify { viewer.show("Project title cannot be empty. Please enter a valid title: ") }
        val projectSlot = slot<Project>()
        verify { updateProjectUseCase.updateProject(capture(projectSlot)) }
        assertEquals(validTitle, projectSlot.captured.title)
    }

    @Test
    fun `promptForUpdateChoice should reject out of range choices`() {
        // Given
        val projects = listOf(projectHelper(id = "project-1", name = "Project 1"))
        val invalidChoice = "3"
        val validChoice = "1"
        val newTitle = "New Title"

        every { getAllProjectsUseCase.getAllProjects() } returns projects
        every { reader.read() } returnsMany listOf("project-1", invalidChoice, validChoice, newTitle, "no")
        every { getProjectByIdUseCase.getProjectById("project-1") } returns projects[0]
        every { updateProjectUseCase.updateProject(any()) } returns Result.success("Success")

        // When
        updateProjectUi.run()

        // Then
        verify { viewer.show("Please enter 1 or 2: ") }
    }

    @Test
    fun `shouldContinueUpdating should handle null input`() {
        // Given
        val projects = listOf(projectHelper(id = "project-1", name = "Project 1"))
        val nullInput = null
        val validInput = "no"
        val newTitle = "New Title"

        every { getAllProjectsUseCase.getAllProjects() } returns projects
        every { reader.read() } returnsMany listOf("project-1", "1", newTitle, nullInput, validInput)
        every { getProjectByIdUseCase.getProjectById("project-1") } returns projects[0]
        every { updateProjectUseCase.updateProject(any()) } returns Result.success("Success")

        // When
        updateProjectUi.run()

        // Then
        verify { viewer.show("Please enter 'yes' or 'no': ") }
    }

    @Test
    fun `updateProjectFields should handle invalid choice exception`() {
        // Given
        val projects = listOf(projectHelper(id = "project-1", name = "Project 1"))
        val invalidChoice = "3"
        val validChoice = "1"
        val newTitle = "New Title"

        every { getAllProjectsUseCase.getAllProjects() } returns projects
        every { reader.read() } returnsMany listOf("project-1", invalidChoice, validChoice, newTitle, "no")
        every { getProjectByIdUseCase.getProjectById("project-1") } returns projects[0]
        every { updateProjectUseCase.updateProject(any()) } returns Result.success("Success")

        // When
        updateProjectUi.run()

        // Then
        verify { viewer.show("Please enter 1 or 2: ") }
        val projectSlot = slot<Project>()
        verify { updateProjectUseCase.updateProject(capture(projectSlot)) }
        assertEquals(newTitle, projectSlot.captured.title)
    }

    @Test
    fun `displayCurrentProjectDetails should show no description when null`() {
        // Given
        val project = projectHelper(id = "project-1", name = "Project 1", description = null)

        // When
        updateProjectUi.displayCurrentProjectDetails(project)

        // Then
        verify { viewer.show("Description: No description\n") }
    }

    @Test
    fun `displayCurrentProjectDetails should show description when present`() {
        // Given
        val description = "Test Description"
        val project = projectHelper(id = "project-1", name = "Project 1", description = description)

        // When
        updateProjectUi.displayCurrentProjectDetails(project)

        // Then
        verify { viewer.show("Description: $description\n") }
    }
}