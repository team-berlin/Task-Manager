package logic.usecase.project;

import com.berlin.logic.repositories.ProjectRepository
import com.berlin.logic.usecase.project.DeleteProjectUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import kotlin.test.Test

class DeleteProjectUseCaseTest {

    private lateinit var deleteProjectUseCase: DeleteProjectUseCase
    private val projectRepository: ProjectRepository = mockk(relaxed = true)

    @BeforeEach
    fun setup() {
        deleteProjectUseCase = DeleteProjectUseCase(projectRepository)
    }

    @Test
    fun `should return success when project deleted successfully`() {
        // Given
        every { projectRepository.deleteProject(any()) } returns Result.success("")

        // When
        val result = deleteProjectUseCase.deleteProject("project_1")

        // Then
        assertThat(result).isEqualTo("Deleted Successfully")
    }

    @Test
    fun `should return failure when project deletion fails`() {
        // Given
        every { projectRepository.deleteProject(any()) } returns Result.failure(Exception())

        // When
        val result = deleteProjectUseCase.deleteProject("1")

        // Then
        assertThat(result).isEqualTo(Exception("Deletion Failed"))
    }

    @Test
    fun `should return null value when project does not exist`() {
        // Given
        val input = "project_1"
        every { projectRepository.getProjectById(any()) } returns null

        // When
        val exception = assertThrows<Exception> { deleteProjectUseCase.deleteProject("P2") }

        // Then
        assertThat(exception.message).isEqualTo(
            "Project with ID $input does not exist"
        )
    }

    @Test
    fun `should return true when project exists`() {
        // Given
        every { projectRepository.getProjectById(any()) } returns mockk()

        // When
        val result = deleteProjectUseCase.deleteProject("P2")

        // Then
        assertThat(result).isEqualTo(true)
    }


    @ParameterizedTest
    @CsvSource(
        "",
        " ",
        "123"
    )
    fun `should throw exception when project ID is invalid`(projectId: String) {
        // Given
        val input = projectId

        // When && Then
        assertThrows<IllegalArgumentException> {
            deleteProjectUseCase.deleteProject(input)
        }
    }

    @Test
    fun `should return true when project id is valid`() {
        // Given
        val projectInput = "project_1"

        // When
        val result = deleteProjectUseCase.deleteProject(projectInput)

        // Then
        assertThat(result).isEqualTo(true)
    }

}