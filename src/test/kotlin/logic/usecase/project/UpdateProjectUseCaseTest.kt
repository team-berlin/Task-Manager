package logic.usecase.project;

import com.berlin.helper.projectHelper
import com.berlin.logic.repositories.ProjectRepository
import com.berlin.logic.usecase.project.UpdateProjectUseCase
import com.berlin.model.Project
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import kotlin.test.Test

class UpdateProjectUseCaseTest {

    private lateinit var updateProjectUseCase: UpdateProjectUseCase
    private val projectRepository: ProjectRepository = mockk(relaxed = true)

    @BeforeEach
    fun setup() {
        updateProjectUseCase = UpdateProjectUseCase(projectRepository)
    }

    @Test
    fun `should return success when project update succeeds`() {
        // Given
        val project = projectHelper()
        every { projectRepository.updateProject(project) } returns Result.success("Updated Successfully")

        // When
        val result = updateProjectUseCase.updateProject(project)

        // Then
        assertThat(result).isEqualTo(Result.success("Updated Successfully"))
    }

    @Test
    fun `should return failure when project update fails`() {
        // Given
        val project = projectHelper()
        every { projectRepository.updateProject(project) } returns Result.failure(Exception())

        // When
        val result = updateProjectUseCase.updateProject(project)

        // Then
        assertThat(result).isEqualTo(Exception("Update Failed"))
    }

    @ParameterizedTest
    @CsvSource(
        ", name, null, [s1,s2,s3] , [t1,t2,t3]",
        "17, name, null, [s1,s2,s3] , [t1,t2,t3]",
        "-, name, null, [s1,s2,s3] , [t1,t2,t3]"
    )
    fun `should throw exception when project ID is invalid`(
        id: String,
        projectName: String,
        description: String,
        statesId: List<String>,
        tasksId: List<String>
    ) {
        // Given
        val input = Project(
            id,
            projectName,
            description,
            statesId,
            tasksId
        )

        // When && Then
        assertThrows<IllegalArgumentException> {
            updateProjectUseCase.updateProject(input)
        }
    }

    @Test
    fun `should return true when project name is valid`() {
        // Given
        val projectInput = projectHelper()

        // When
        val result = updateProjectUseCase.updateProject(projectInput)

        // Then
        assertThat(result).isEqualTo(true)
    }

}