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
    fun `should delete project successfully when valid project id is provided`() {
        // Given
        val validProjectId = "project1"
        every { reader.getUserInput() } returns validProjectId
        every { deleteProjectUseCase.deleteProject(validProjectId) } returns Result.success("Deleted Successfully")

        // When
        deleteProjectUi.run()

        // Then
        verify { deleteProjectUseCase.deleteProject(validProjectId) }
        verify { viewer.display("Project deleted Successfully!\n") }
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
    fun `should throw exception when project id is null`() {
        // Given
        every { reader.getUserInput() } returns null

        // When & Then
        assertThrows<Exception>("Project id can not be null") {
            deleteProjectUi.run()
        }
    }

    @Test
    fun `should throw exception when project id is not valid`() {
        // Given
        val invalidProjectId = "nonExistingId"
        every { reader.getUserInput() } returns invalidProjectId

        // When & Then
        assertThrows<Exception>("Please enter a valid project id") {
            deleteProjectUi.run()
        }
    }


}
