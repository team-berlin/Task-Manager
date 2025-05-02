package presentation.project;

import com.berlin.helper.projectHelper
import com.berlin.logic.usecase.project.GetAllProjectsUseCase
import com.berlin.presentation.input_output.Viewer
import com.berlin.presentation.project.GetAllProjectsUi
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

class GetAllProjectsUiTest {

    private lateinit var getAllProjectsUseCase: GetAllProjectsUseCase
    private lateinit var getAllProjectsUi: GetAllProjectsUi
    private val viewer: Viewer = mockk(relaxed = true)

    @BeforeEach
    fun setup() {
        getAllProjectsUseCase = mockk()
        getAllProjectsUi = GetAllProjectsUi(getAllProjectsUseCase, viewer)
    }

    @Test
    fun `Should display all projects list`() {
        // Given
        every { getAllProjectsUseCase.getAllProjects() } returns listOf(projectHelper(),projectHelper(),projectHelper())

        // When
        getAllProjectsUi.run()

        // Then
        verify { viewer.display(any()) }
    }

    @Test
    fun `Should return projects list however there is only one project available`() {
        // Given
        every { getAllProjectsUseCase.getAllProjects() } returns listOf(projectHelper())

        // When
        getAllProjectsUi.run()

        // Then
        verify { viewer.display(any()) }
    }

    @Test
    fun `Should return failed message when there no available projects`() {
        // Given
        every { getAllProjectsUseCase.getAllProjects() } returns emptyList()

        // When
        getAllProjectsUi.run()

        // Then
        verify { viewer.display("No projects available.\n") }


    }

}
