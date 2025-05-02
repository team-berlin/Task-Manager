package presentation.project;

import com.berlin.logic.usecase.project.GetProjectByIdUseCase
import com.berlin.model.Project
import com.berlin.presentation.input_output.Reader
import com.berlin.presentation.input_output.Viewer
import com.berlin.presentation.project.GetProjectByIdUi
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

class GetProjectByIdUiTest {

    private lateinit var getProjectByIdUseCase: GetProjectByIdUseCase
    private lateinit var getProjectByIdUi: GetProjectByIdUi
    private val viewer: Viewer = mockk(relaxed = true)
    private val reader: Reader = mockk(relaxed = true)

    @BeforeEach
    fun setup() {
        getProjectByIdUseCase = mockk(relaxed = true)
        getProjectByIdUi = GetProjectByIdUi(getProjectByIdUseCase, viewer, reader)
    }

    @Test
    fun `run should display project details when valid project id is entered`() {
        // Given
        val projectId = "project-123"
        val project = Project(
            id = projectId,
            name = "Test Project",
            statesId = listOf("state-1", "state-2"),
            tasksId = listOf("task-1", "task-2"),
            description = "description"
        )

        every { reader.getUserInput() } returns projectId
        every { getProjectByIdUseCase.getProjectById(projectId) } returns project

        // When
        getProjectByIdUi.run()

        // Then
        verify { reader.getUserInput() }
        verify { getProjectByIdUseCase.getProjectById(projectId) }
    }

    @Test
    fun `run should throw exception when user input is null`() {
        // Given
        every { reader.getUserInput() } returns null

        // When & Then
        assertThrows<Exception> { getProjectByIdUi.run() }

        // Verify
        verify { reader.getUserInput() }
    }

    @Test
    fun `run should throw exception when project does not exist`() {
        // Given
        val projectId = "non-existent-id"
        val exception = Exception("Project with ID $projectId does not exist")

        every { reader.getUserInput() } returns projectId
        every { getProjectByIdUseCase.getProjectById(projectId) } throws exception

        // When & Then
        val thrownException = assertThrows<Exception> { getProjectByIdUi.run() }

        // Verify
        verify { reader.getUserInput() }
        verify { getProjectByIdUseCase.getProjectById(projectId) }
        assert(thrownException.message == exception.message)
    }

}