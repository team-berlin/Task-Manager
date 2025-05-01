package logic.usecase.project;

import com.berlin.helper.projectHelper
import com.berlin.logic.generateIdHelper.DefaultIdGenerator
import com.berlin.logic.repositories.ProjectRepository
import com.berlin.logic.usecase.project.CreateProjectUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.Test

class CreateProjectUseCaseTest {

    private lateinit var createProjectUseCase: CreateProjectUseCase
    private val projectRepository: ProjectRepository = mockk(relaxed = true)


    @BeforeEach
    fun setup() {
        val idGenerator: DefaultIdGenerator = mockk(relaxed = true)
        createProjectUseCase = CreateProjectUseCase(projectRepository, idGenerator)
    }

    @Test
    fun `createNewProject should return success when project created successfully`() {
        // Given
        val validProject = projectHelper()
        every { projectRepository.createProject(any()) } returns Result.success("Creation Successfully")

        // When
        val result = createProjectUseCase.createNewProject(
            validProject.name,
            validProject.description,
            validProject.statesId,
            validProject.tasksId
        )

        // Then
        assertThat(result).isEqualTo(Result.success("Creation Successfully"))
    }

    @Test
    fun `createNewProject should return failure when project creation fails`() {
        // Given
        val validProject = projectHelper()
        every { projectRepository.createProject(any()) } returns Result.failure(Exception())

        // When
        val result = createProjectUseCase.createNewProject(
            validProject.name,
            validProject.description,
            validProject.statesId,
            validProject.tasksId
        )

        // Then
        result.onFailure { exception ->
            assertThat(exception.message).isEqualTo("Creation Failed")
        }
    }


    @ParameterizedTest
    @ValueSource(strings = ["", " ", "123"])
    fun `validateProjectName should throw exception when project name is invalid`(
        invalidName: String
    ) {
        // When && Then
        assertThrows<Exception> {
            createProjectUseCase.createNewProject(invalidName,
                null,
                null,
                null
            )
        }
    }

}