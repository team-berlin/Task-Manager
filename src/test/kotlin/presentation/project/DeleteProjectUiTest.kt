package presentation.project;

import com.berlin.helper.projectHelper
import com.berlin.logic.usecase.project.DeleteProjectUseCase
import com.berlin.logic.usecase.project.GetAllProjectsUseCase
import com.berlin.model.Project
import com.berlin.presentation.input_output.Reader
import com.berlin.presentation.input_output.Viewer
import com.berlin.presentation.project.DeleteProjectUi
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.Test

class DeleteProjectUiTest {

    private lateinit var deleteProjectUseCase: DeleteProjectUseCase
    private lateinit var getAllProjectsUseCase: GetAllProjectsUseCase
    private lateinit var deleteProjectUi: DeleteProjectUi
    private val viewer: Viewer = mockk(relaxed = true)
    private val reader: Reader = mockk(relaxed = true)

    private val testProjects = listOf(
        Project(
            id = "project1",
            name = "First Project",
            description = "Test description",
            statesId = null,
            tasksId = null
        ),
        Project(
            id = "project2",
            name = "Second Project",
            description = "Test description",
            statesId = null,
            tasksId = null
        )
    )

    @BeforeEach
    fun setup() {
        deleteProjectUseCase = mockk(relaxed = true)
        getAllProjectsUseCase = mockk(relaxed = true)
        deleteProjectUi = DeleteProjectUi(deleteProjectUseCase, getAllProjectsUseCase, viewer, reader)

        every { getAllProjectsUseCase.getAllProjects() } returns testProjects
    }

    @Test
    fun `should display failure message when deletion process fails`() {
        // Given
        val validProjectId = "project1"
        every { reader.getUserInput() } returns validProjectId
        every { deleteProjectUseCase.deleteProject(validProjectId) } returns Result.failure(Exception("Deletion Failed"))

        // When
        deleteProjectUi.run()

        // Then
        verify { deleteProjectUseCase.deleteProject(validProjectId) }
        verify { viewer.display("Project deletion failed!\n") }
    }

    @Test
    fun `should display success message when deletion succeeds`() {
        // Given
        val validProjectId = "project1"
        every { reader.getUserInput() } returns validProjectId
        every { deleteProjectUseCase.deleteProject(validProjectId) } returns Result.success("Deleted Successfully")

        // When
        deleteProjectUi.run()

        // Then
        verify { deleteProjectUseCase.deleteProject(validProjectId) }
        verify { viewer.display("Project deleted successfully!\n") }
    }

    @Test
    fun `should display return message when no projects available`() {
        // Given
        every { getAllProjectsUseCase.getAllProjects() } returns emptyList()

        // When
        deleteProjectUi.run()

        // Then
        verify { viewer.display("No projects available to delete.\n") }
    }

    @Test
    fun `should take input from the user again when invalid project ID is provided`() {
        // Given
        val invalidId = "invalidId"
        val validId = "project1"
        every { reader.getUserInput() } returnsMany listOf(invalidId, validId)

        // When
        deleteProjectUi.run()

        // Then
        verify { viewer.display("Please enter a valid project id from the list above:") }
        verify { deleteProjectUseCase.deleteProject(validId) }
    }

    @Test
    fun `should handle null input for project ID`() {
        // Given
        val validId = "project1"
        every { reader.getUserInput() } returnsMany listOf(null, validId)

        // When
        deleteProjectUi.run()

        // Then
        verify { viewer.display("Please enter a valid project id from the list above:") }
        verify { deleteProjectUseCase.deleteProject(validId) }
    }

    @Test
    fun `should continue taking input from the user until valid project ID is provided`() {
        // Given
        val invalidId1 = "invalid1"
        val invalidId2 = "invalid2"
        val nullInput = null
        val validId = "project2"

        every { reader.getUserInput() } returnsMany listOf(invalidId1, invalidId2, nullInput, validId)

        // When
        deleteProjectUi.run()

        // Then
        verify(exactly = 3) { viewer.display("Please enter a valid project id from the list above:") }
        verify { deleteProjectUseCase.deleteProject(validId) }
    }

    @ParameterizedTest
    @ValueSource(strings = ["project1", "project2"])
    fun `should accept any valid project ID from the list`(projectId: String) {
        // Given
        every { reader.getUserInput() } returns projectId
        every { deleteProjectUseCase.deleteProject(projectId) } returns Result.success("Deleted Successfully")

        // When
        deleteProjectUi.run()

        // Then
        verify { deleteProjectUseCase.deleteProject(projectId) }
        verify { viewer.display("Project deleted successfully!\n") }
    }
}
