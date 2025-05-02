package logic.usecase.project;

import com.berlin.logic.repositories.ProjectRepository
import com.berlin.logic.usecase.project.DeleteProjectUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
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
        assertThat(result).isEqualTo(Result.success("Deleted Successfully"))
    }

    @Test
    fun `should return failure when project deletion fails`() {
        // Given
        every { projectRepository.deleteProject("P1") } returns Result.failure(Exception())

        // When
        val result = deleteProjectUseCase.deleteProject("P1")

        // Then
        result.onFailure { exception ->
            assertThat(exception.message).isEqualTo("Deletion Failed")
        }
    }

    @Test
    fun `should throw exception when project id does not exists`() {
        // Given
        every { projectRepository.getProjectById(any()) } returns null

        // When
        val result = deleteProjectUseCase.deleteProject("P2")

        // Then
        result.onFailure { exception ->
            assertThat(exception.message).isEqualTo("Project with ID P2 does not exist")
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ["", " ", "123"])
    fun `should throw exception when project ID is invalid`(projectId: String) {
        // When && Then
        assertThrows<Exception> {
            deleteProjectUseCase.deleteProject(projectId)
        }
    }

}