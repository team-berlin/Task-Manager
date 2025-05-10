package logic.usecase.project;

import com.berlin.helper.projectHelper
import com.berlin.domain.repository.ProjectRepository
import com.berlin.domain.usecase.project.UpdateProjectUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.Test

class UpdateProjectUseCaseTest {

    private lateinit var updateProjectUseCase: UpdateProjectUseCase
    private val projectRepository: ProjectRepository = mockk(relaxed = true)

    @BeforeEach
    fun setup() {
        updateProjectUseCase = UpdateProjectUseCase(projectRepository)
    }

    @Test
    fun `should return success when project update succeeds`() = runTest {
        // Given
        val project = projectHelper()
        coEvery { projectRepository.updateProject(project) } returns Result.success("Updated Successfully")

        // When
        val result = updateProjectUseCase.updateProject(project)

        // Then
        assertThat(result).isEqualTo(Result.success("Updated Successfully"))
    }

    @Test
    fun `should return failure when project update fails`() = runTest {
        // Given
        val project = projectHelper()
        coEvery { projectRepository.updateProject(project) } returns Result.failure(Exception())

        // When
        val result = updateProjectUseCase.updateProject(project)

        // Then
        result.onFailure { exception ->
            assertThat(exception.message).isEqualTo("Update Failed")
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ["", " ", "123"])
    fun `should throw exception when project ID is invalid`(
        invalidName: String
    ) = runTest {
        // When && Then
        assertThrows<Exception> {
            updateProjectUseCase.updateProject(
                projectHelper(
                    name = invalidName
                )
            )
        }
    }
}