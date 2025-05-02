package presentation.project;

import com.berlin.helper.projectHelper
import com.berlin.logic.usecase.project.GetAllProjectsUseCase
import com.berlin.logic.usecase.project.GetProjectByIdUseCase
import com.berlin.logic.usecase.project.UpdateProjectUseCase
import com.berlin.model.Project
import com.berlin.presentation.input_output.Reader
import com.berlin.presentation.input_output.Viewer
import com.berlin.presentation.project.UpdateProjectUi
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

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
            projectHelper(id = "project-1",name = "Project 1"),
            projectHelper(id = "project-2",name = "Project 2")
        )
        val projectId = "project-1"
        val project = projects[0]
        val newTitle = "Updated Project 1"

        every { getAllProjectsUseCase.getAllProjects() } returns projects
        every { reader.getUserInput() } returnsMany listOf(projectId, "1", newTitle, "no")
        every { getProjectByIdUseCase.getProjectById(projectId) } returns project
        every { updateProjectUseCase.updateProject(any()) } returns Result.success("Updated Successfully")

        // When
        updateProjectUi.run()

        // Then
        verify { getAllProjectsUseCase.getAllProjects() }
        verify { getProjectByIdUseCase.getProjectById(projectId) }
        val projectSlot = slot<Project>()
        verify { updateProjectUseCase.updateProject(capture(projectSlot)) }
        assert(projectSlot.captured.name == newTitle)
        verify { viewer.display("Project updated successfully!\n") }
    }

    @Test
    fun `run should update project description successfully`() {
        // Given
        val projects = listOf(
            projectHelper(id = "project-1",name = "Project 1", description = "Description 1"),
            projectHelper(id = "project-2",name = "Project 2", description = "Description 2")
        )
        val projectId = "project-1"
        val project = projects[0]
        val newDescription = "This is an updated description"

        every { getAllProjectsUseCase.getAllProjects() } returns projects
        every { reader.getUserInput() } returnsMany listOf(projectId, "2", newDescription, "no")
        every { getProjectByIdUseCase.getProjectById(projectId) } returns project
        every { updateProjectUseCase.updateProject(any()) } returns Result.success("Updated Successfully")

        // When
        updateProjectUi.run()

        // Then
        verify { getAllProjectsUseCase.getAllProjects() }
        verify { getProjectByIdUseCase.getProjectById(projectId) }
        val projectSlot = slot<Project>()
        verify { updateProjectUseCase.updateProject(capture(projectSlot)) }
        assert(projectSlot.captured.description == newDescription)
        verify { viewer.display("Project updated successfully!\n") }
    }

    @Test
    fun `run should update multiple fields when user selects yes to continue`() {
        // Given
        val projects = listOf(
            projectHelper(id = "project-1",name = "Project 1", description = "Description 1")
        )
        val projectId = "project-1"
        val project = projects[0]
        val newTitle = "Updated Project 1"
        val newDescription = "This is an updated description"

        every { getAllProjectsUseCase.getAllProjects() } returns projects
        every { reader.getUserInput() } returnsMany listOf(
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
        assert(projectSlot.captured.name == newTitle)
        assert(projectSlot.captured.description == newDescription)
        verify { viewer.display("Project updated successfully!\n") }
    }

    @Test
    fun `run should display failure message when update fails`() {
        // Given
        val projects = listOf(
            projectHelper(id = "project-1",name = "Project 1", description = "Description 1")
        )
        val projectId = "project-1"
        val project = projects[0]
        val newTitle = "Updated Project 1"

        every { getAllProjectsUseCase.getAllProjects() } returns projects
        every { reader.getUserInput() } returnsMany listOf(projectId, "1", newTitle, "no")
        every { getProjectByIdUseCase.getProjectById(projectId) } returns project
        every { updateProjectUseCase.updateProject(any()) } returns Result.failure(Exception("Update failed"))

        // When
        updateProjectUi.run()

        // Then
        verify { getAllProjectsUseCase.getAllProjects() }
        verify { getProjectByIdUseCase.getProjectById(projectId) }
        verify { updateProjectUseCase.updateProject(any()) }
        verify { viewer.display("Project update failed!\n") }
    }

    @Test
    fun `run should throw exception when project id is null`() {
        // Given
        val projects = listOf(
            projectHelper(id = "project-1",name = "Project 1", description = "Description 1")
        )

        every { getAllProjectsUseCase.getAllProjects() } returns projects
        every { reader.getUserInput() } returns null

        // When & Then
        assertThrows<Exception> { updateProjectUi.run() }

        verify { getAllProjectsUseCase.getAllProjects() }
        verify { reader.getUserInput() }
    }

    @Test
    fun `run should throw exception when project id is invalid`() {
        // Given
        val projects = listOf(
            projectHelper(id = "project-1",name = "Project 1", description = "Description 1")
        )
        val invalidProjectId = "invalid-id"

        every { getAllProjectsUseCase.getAllProjects() } returns projects
        every { reader.getUserInput() } returns invalidProjectId

        // When & Then
        assertThrows<Exception> { updateProjectUi.run() }

        verify { getAllProjectsUseCase.getAllProjects() }
        verify { reader.getUserInput() }
    }

    @Test
    fun `run should throw exception when project name is null`() {
        // Given
        val projects = listOf(
            projectHelper(id = "project-1",name = "Project 1", description = "Description 1")
        )
        val projectId = "project-1"
        val project = projects[0]

        every { getAllProjectsUseCase.getAllProjects() } returns projects
        every { reader.getUserInput() } returnsMany listOf(projectId, "1", null)
        every { getProjectByIdUseCase.getProjectById(projectId) } returns project

        // When & Then
        assertThrows<Exception> { updateProjectUi.run() }

        verify { getAllProjectsUseCase.getAllProjects() }
        verify { getProjectByIdUseCase.getProjectById(projectId) }
        verify { reader.getUserInput() }
    }

    @Test
    fun `run should throw exception when invalid option is selected for continue`() {
        // Given
        val projects = listOf(
            projectHelper(id = "project-1",name = "Project 1", description = "Description 1")
        )
        val projectId = "project-1"
        val project = projects[0]
        val newTitle = "Updated Project 1"

        every { getAllProjectsUseCase.getAllProjects() } returns projects
        every { reader.getUserInput() } returnsMany listOf(projectId, "1", newTitle, "invalid")
        every { getProjectByIdUseCase.getProjectById(projectId) } returns project

        // When & Then
        assertThrows<Exception> { updateProjectUi.run() }

        assert(project.name == newTitle)
    }

    @Test
    fun `run should throw exception when invalid field option is selected`() {
        // Given
        val projects = listOf(
            projectHelper(id = "project-1",name = "Project 1", description = "Description 1")
        )
        val projectId = "project-1"
        val project = projects[0]

        every { getAllProjectsUseCase.getAllProjects() } returns projects
        every { reader.getUserInput() } returnsMany listOf(projectId, "3")
        every { getProjectByIdUseCase.getProjectById(projectId) } returns project

        // When & Then
        assertThrows<Exception> { updateProjectUi.run() }

        verify { getAllProjectsUseCase.getAllProjects() }
        verify { getProjectByIdUseCase.getProjectById(projectId) }
    }


    @Test
    fun `run should throw exception when field selection input is not numeric`() {
        // Given
        val projects = listOf(
            projectHelper(id = "project-1",name = "Project 1", description = "Description 1")
        )
        val projectId = "project-1"
        val project = projects[0]

        every { getAllProjectsUseCase.getAllProjects() } returns projects
        every { reader.getUserInput() } returnsMany listOf(projectId, "not-a-number")
        every { getProjectByIdUseCase.getProjectById(projectId) } returns project

        // When & Then
        assertThrows<NumberFormatException> { updateProjectUi.run() }

        verify { getAllProjectsUseCase.getAllProjects() }
        verify { getProjectByIdUseCase.getProjectById(projectId) }
    }

    @Test
    fun `run should throw exception when field selection input is null`() {
        // Given
        val projects = listOf(
            projectHelper(id = "project-1",name = "Project 1", description = "Description 1")
        )
        val projectId = "project-1"
        val project = projects[0]

        every { getAllProjectsUseCase.getAllProjects() } returns projects
        every { reader.getUserInput() } returnsMany listOf(projectId, null)
        every { getProjectByIdUseCase.getProjectById(projectId) } returns project

        // When & Then
        assertThrows<Exception> { updateProjectUi.run() }

        verify { getAllProjectsUseCase.getAllProjects() }
        verify { getProjectByIdUseCase.getProjectById(projectId) }
    }

    @Test
    fun `run should throw exception when field selection input is negative`() {
        // Given
        val projects = listOf(
            projectHelper(id = "project-1",name = "Project 1", description = "Description 1")
        )
        val projectId = "project-1"
        val project = projects[0]

        every { getAllProjectsUseCase.getAllProjects() } returns projects
        every { reader.getUserInput() } returnsMany listOf(projectId, "-1")
        every { getProjectByIdUseCase.getProjectById(projectId) } returns project

        // When & Then
        assertThrows<Exception> { updateProjectUi.run() }

        verify { getAllProjectsUseCase.getAllProjects() }
        verify { getProjectByIdUseCase.getProjectById(projectId) }
    }

    @Test
    fun `run should throw exception when field selection input is zero`() {
        // Given
        val projects = listOf(
            projectHelper(id = "project-1",name = "Project 1", description = "Description 1")
        )
        val projectId = "project-1"
        val project = projects[0]

        every { getAllProjectsUseCase.getAllProjects() } returns projects
        every { reader.getUserInput() } returnsMany listOf(projectId, "0")
        every { getProjectByIdUseCase.getProjectById(projectId) } returns project

        // When & Then
        assertThrows<Exception> { updateProjectUi.run() }

        verify { getAllProjectsUseCase.getAllProjects() }
        verify { getProjectByIdUseCase.getProjectById(projectId) }
    }

    @Test
    fun `run should exit update loop when user enters no`() {
        // Given
        val projects = listOf(
            projectHelper(id = "project-1",name = "Project 1", description = "Description 1")
        )
        val projectId = "project-1"
        val project = projects[0]
        val newTitle = "Updated Project 1"

        every { getAllProjectsUseCase.getAllProjects() } returns projects
        every { reader.getUserInput() } returnsMany listOf(projectId, "1", newTitle, "no")
        every { getProjectByIdUseCase.getProjectById(projectId) } returns project
        every { updateProjectUseCase.updateProject(any()) } returns Result.success("Updated Successfully")

        // When
        updateProjectUi.run()

        // Then
        verify(exactly = 1) { updateProjectUseCase.updateProject(any()) }
        verify { viewer.display("Project updated successfully!\n") }
    }

    @Test
    fun `run should continue update loop when user enters yes`() {
        // Given
        val projects = listOf(
            projectHelper(id = "project-1",name = "Project 1", description = "Description 1")
        )
        val projectId = "project-1"
        val project = projects[0]
        val newTitle = "Updated Project 1"
        val newDescription = "Updated Description"

        every { getAllProjectsUseCase.getAllProjects() } returns projects
        every { reader.getUserInput() } returnsMany listOf(
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
        assert(projectSlot.captured.name == newTitle)
        assert(projectSlot.captured.description == newDescription)
        verify(exactly = 1) { updateProjectUseCase.updateProject(any()) }
    }

    @Test
    fun `run should accept uppercase YES and NO options`() {
        // Given
        val projects = listOf(
            projectHelper(id = "project-1",name = "Project 1", description = "Description 1")
        )
        val projectId = "project-1"
        val project = projects[0]
        val newTitle = "Updated Title"
        val newDescription = "Updated Description"

        every { getAllProjectsUseCase.getAllProjects() } returns projects
        every { reader.getUserInput() } returnsMany listOf(
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
        assert(projectSlot.captured.name == newTitle)
        assert(projectSlot.captured.description == newDescription)
    }

    @Test
    fun `run should accept mixed case yes and no options`() {
        // Given
        val projects = listOf(
            projectHelper(id = "project-1",name = "Project 1", description = "Description 1")
        )
        val projectId = "project-1"
        val project = projects[0]
        val newTitle = "Updated Title"
        val newDescription = "Updated Description"

        every { getAllProjectsUseCase.getAllProjects() } returns projects
        every { reader.getUserInput() } returnsMany listOf(
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
        assert(projectSlot.captured.name == newTitle)
        assert(projectSlot.captured.description == newDescription)
    }

    @Test
    fun `run should throw exception when null option is provided for continuation`() {
        // Given
        val projects = listOf(
            projectHelper(id = "project-1",name = "Project 1", description = "Description 1")
        )
        val projectId = "project-1"
        val project = projects[0]
        val newTitle = "Updated Title"

        every { getAllProjectsUseCase.getAllProjects() } returns projects
        every { reader.getUserInput() } returnsMany listOf(
            projectId,
            "1", newTitle, null
        )
        every { getProjectByIdUseCase.getProjectById(projectId) } returns project

        // When & Then
        assertThrows<Exception> { updateProjectUi.run() }

        verify(exactly = 0) { updateProjectUseCase.updateProject(any()) }
    }

    @Test
    fun `run should throw exception when invalid option is provided for continuation`() {
        // Given
        val projects = listOf(
            projectHelper(id = "project-1",name = "Project 1", description = "Description 1")
        )
        val projectId = "project-1"
        val project = projects[0]
        val newTitle = "Updated Title"

        every { getAllProjectsUseCase.getAllProjects() } returns projects
        every { reader.getUserInput() } returnsMany listOf(
            projectId,
            "1", newTitle, "maybe"
        )
        every { getProjectByIdUseCase.getProjectById(projectId) } returns project

        // When & Then
        assertThrows<Exception> { updateProjectUi.run() }

        verify(exactly = 0) { updateProjectUseCase.updateProject(any()) }
    }
}