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
import kotlin.test.Test

class CreateProjectUseCaseTest {

    private lateinit var createProjectUseCase: CreateProjectUseCase
    private val projectRepository: ProjectRepository = mockk(relaxed = true)


    @BeforeEach
    fun setup() {
        val idGenerator: DefaultIdGenerator = mockk()
        createProjectUseCase = CreateProjectUseCase(projectRepository, idGenerator)
    }

    @Test
    fun `createNewProject should return success when project created successfully`() {
        // Given
        val validProject = projectHelper()
        every { projectRepository.createProject(any()) } returns Result.success("")

        // When
        val result = createProjectUseCase.createNewProject(
            validProject.name,
            validProject.statesId,
            validProject.tasksId
        )

        // Then
        assertThat(result).isEqualTo(
            Result.success("Project created successfully")
        )
    }

    @Test
    fun `createNewProject should return failure when project creation fails`() {
        // Given
        val validProject = projectHelper()
        every { projectRepository.createProject(any()) } returns Result.failure(Exception())

        // When
        val result = createProjectUseCase.createNewProject(
            validProject.name,
            validProject.statesId,
            validProject.tasksId
        )

        // Then
        assertThat(result).isEqualTo(Exception("Project creation failed"))
    }

    @ParameterizedTest
    @CsvSource(
        "",
        " ",
        "123"
    )
    fun `validateProjectName should throw exception when project name is invalid`(
        invalidName: String
    ) {
        // Given
        val projectInput = projectHelper(name = invalidName)

        // When && Then
        assertThrows<IllegalArgumentException> {
            createProjectUseCase.createNewProject(
                projectInput.name,
                projectInput.statesId,
                projectInput.tasksId
            )
        }
    }

    @Test
    fun `validateProjectName should return true when project name is valid`() {
        // Given
        val projectInput = projectHelper()

        // When
        val result = createProjectUseCase.createNewProject(
            projectInput.name,
            projectInput.statesId,
            projectInput.tasksId
        )

        // Then
        assertThat(result).isEqualTo(true)
    }

}