package presentation.project

import com.berlin.domain.usecase.project.GetProjectByIdUseCase
import com.berlin.domain.model.Project
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import com.berlin.presentation.project.GetProjectByIdUi
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class GetProjectByIdUiTest {

    private lateinit var getProjectByIdUseCase: GetProjectByIdUseCase
    private lateinit var getProjectByIdUi: GetProjectByIdUi
    private val viewer: Viewer = mockk(relaxed = true)
    private val reader: Reader = mockk(relaxed = true)

    private val testProjects =
        Project(
            id = "project-123",
            name = "Test Project",
            statesId = listOf("state-1", "state-2"),
            tasksId = listOf("task-1", "task-2"),
            description = "Test project description"
        )

    @BeforeEach
    fun setup() {
        getProjectByIdUseCase = mockk(relaxed = true)
        getProjectByIdUi = GetProjectByIdUi(getProjectByIdUseCase, viewer, reader)
    }

    @Test
    fun `run should display complete project details when valid project id is entered`() {
        // Given
        val projectId = testProjects.id
        every { reader.read() } returns projectId
        every { getProjectByIdUseCase.getProjectById(projectId) } returns testProjects

        // When
        getProjectByIdUi.run()

        // Then
        verify { reader.read() }
        verify { getProjectByIdUseCase.getProjectById(projectId) }
    }

    @Test
    fun `run should handle empty inputs and take another valid input`() {
        // Given
        val projectId = testProjects.id

        every { reader.read() } returnsMany listOf("", projectId)
        every { getProjectByIdUseCase.getProjectById(projectId) } returns testProjects

        // When
        getProjectByIdUi.run()

        // Then
        verify(exactly = 2) { reader.read() }
        verify { getProjectByIdUseCase.getProjectById(projectId) }
    }

    @Test
    fun `run should handle whitespace inputs and take another valid input`() {
        // Given
        val projectId = testProjects.id
        every { reader.read() } returnsMany listOf("   ", projectId)
        every { getProjectByIdUseCase.getProjectById(projectId) } returns testProjects

        // When
        getProjectByIdUi.run()

        // Then
        verify(exactly = 2) { reader.read() }
        verify { getProjectByIdUseCase.getProjectById(projectId) }
    }

    @Test
    fun `run should display error message when retrieving project fails`() {
        // Given
        val projectId = "invalid-id"
        val errorMessage = "Project not found"
        val exception = Exception(errorMessage)

        every { reader.read() } returns projectId
        every { getProjectByIdUseCase.getProjectById(projectId) } throws exception

        // When
        getProjectByIdUi.run()

        // Then
        verify { reader.read() }
        verify { getProjectByIdUseCase.getProjectById(projectId) }
        verify { viewer.show("Error retrieving project: $errorMessage\n") }
    }

    @Test
    fun `run should display project with null description correctly`() {
        // Given
        val projectId = testProjects.id
        val project = testProjects.copy(description = null)

        every { reader.read() } returns projectId
        every { getProjectByIdUseCase.getProjectById(projectId) } returns project

        // When
        getProjectByIdUi.run()

        // Then
        verify { getProjectByIdUseCase.getProjectById(projectId) }
        verify { viewer.show("=== Project Description: No description ===\n") }
    }

    @Test
    fun `run should display message when project has no states`() {
        // Given
        val projectId = testProjects.id
        val project = testProjects.copy(statesId = null)


        every { reader.read() } returns projectId
        every { getProjectByIdUseCase.getProjectById(projectId) } returns project

        // When
        getProjectByIdUi.run()

        // Then
        verify { getProjectByIdUseCase.getProjectById(projectId) }
        verify { viewer.show("No states defined for this project.\n") }
    }

    @Test
    fun `run should display message when state has no tasks`() {
        // Given
        val projectId = testProjects.id
        val project = testProjects.copy(statesId = listOf("state-1"), tasksId = null)

        every { reader.read() } returns projectId
        every { getProjectByIdUseCase.getProjectById(projectId) } returns project

        // When
        getProjectByIdUi.run()

        // Then
        verify { getProjectByIdUseCase.getProjectById(projectId) }
        verify { viewer.show("State: [state-1] state-1\n") }
        verify { viewer.show("  No tasks for this state.\n\n") }
    }
}

