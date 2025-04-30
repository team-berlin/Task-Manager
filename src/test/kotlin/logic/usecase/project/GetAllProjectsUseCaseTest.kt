package logic.usecase.project;

import com.berlin.helper.projectHelper
import com.berlin.logic.repositories.ProjectRepository
import com.berlin.logic.usecase.project.GetAllProjectsUseCase;
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

class GetAllProjectsUseCaseTest {

    private lateinit var getAllProjectsUseCase: GetAllProjectsUseCase
    private val projectRepository: ProjectRepository = mockk(relaxed = true)

    @BeforeEach
    fun setup() {
        getAllProjectsUseCase = GetAllProjectsUseCase(projectRepository)
    }

    @Test
    fun `should return list of projects when valid projects exists`() {
        // Given
        val expectedProjectsList = listOf(projectHelper(),projectHelper(),projectHelper())
        every { projectRepository.getAllProjects() } returns expectedProjectsList

        // When
        val result = getAllProjectsUseCase.getAllProjects()

        // Then
        assertThat(result).isEqualTo(expectedProjectsList)
    }

    @Test
    fun `should throw exception when there is no project exist`() {
        // Given
        every { projectRepository.getAllProjects() } returns emptyList()

        // When
        val exception = assertThrows<Exception> { getAllProjectsUseCase.getAllProjects() }

        // Then
        assertThat(exception.message).isEqualTo("No projects found")
    }
    
}