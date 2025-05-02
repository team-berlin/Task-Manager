package presentation.project;

import com.berlin.logic.usecase.project.CreateProjectUseCase
import com.berlin.presentation.input_output.Reader
import com.berlin.presentation.input_output.Viewer
import com.berlin.presentation.project.CreateProjectUi
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.Test

class CreateProjectUiTest {

    private lateinit var createProjectUseCase: CreateProjectUseCase
    private lateinit var createProjectUi: CreateProjectUi
    private val viewer: Viewer = mockk(relaxed = true)
    private val reader: Reader = mockk(relaxed = true)


    @BeforeEach
    fun setup() {
        createProjectUseCase = mockk(relaxed = true)
        createProjectUi = CreateProjectUi(createProjectUseCase, viewer, reader)
    }

    @Test
    fun `should throw exception when the project name input is null`() {
        // Given
        every { reader.getUserInput() } returns null

        //When && Then
        assertThrows<Exception> {
            createProjectUi.run()
        }
    }

    @Test
    fun `should create a project successfully when valid project name provided with no description`() {
        // Given
        val validProjectName = "My Project"
        every { reader.getUserInput() } returnsMany listOf(validProjectName, "no")

        // When
        createProjectUi.run()

        // Then
        verify { createProjectUseCase.createNewProject(validProjectName, null, null, null) }
        verify { viewer.display("Project created successfully!\n") }
    }

    @Test
    fun `should create a project successfully when user enter a description`() {
        // Given
        val validProjectName = "My Project"
        val projectDescription = "This is a test project"
        every { reader.getUserInput() } returnsMany listOf(validProjectName, "yes", projectDescription)

        // When
        createProjectUi.run()

        // Then
        verify { createProjectUseCase.createNewProject(validProjectName, projectDescription, null, null) }
        verify { viewer.display("Project created successfully!\n") }
    }

    @Test
    fun `should handle null project name input by retrying`() {
        // Given
        val validProjectName = "Valid Project"
        every { reader.getUserInput() } returnsMany listOf(null, validProjectName)

        // When & Then
        assertThrows<Exception> {
            createProjectUi.run()
        }
    }

    @Test
    fun `should handle case-insensitive 'yes' for description option`() {
        // Given
        val validProjectName = "Project X"
        val projectDescription = "Description for Project X"
        every { reader.getUserInput() } returnsMany listOf(validProjectName, "YES", projectDescription)

        // When
        createProjectUi.run()

        // Then
        verify { createProjectUseCase.createNewProject(validProjectName, projectDescription, null, null) }
    }

    @Test
    fun `should pass null description when user provides anything other than 'yes'`() {
        // Given
        val validProjectName = "Another Project"
        every { reader.getUserInput() } returnsMany listOf(validProjectName, "nope")

        // When
        createProjectUi.run()

        // Then
        verify { createProjectUseCase.createNewProject(validProjectName, null, null, null) }
    }

    @Test
    fun `should handle mixed case 'yes' for description option`() {
        // Given
        val validProjectName = "Project Y"
        val projectDescription = "This is another description"
        every { reader.getUserInput() } returnsMany listOf(validProjectName, "YeS", projectDescription)

        // When
        createProjectUi.run()

        // Then
        verify { createProjectUseCase.createNewProject(validProjectName, projectDescription, null, null) }
    }

    @ParameterizedTest
    @ValueSource(strings = ["no", "nope", "n", "", " "])
    fun `should pass null description when user provides anything other than 'yes'`(noResponse: String) {
        // Given
        val validProjectName = "Another Project"
        every { reader.getUserInput() } returnsMany listOf(validProjectName, noResponse)

        // When
        createProjectUi.run()

        // Then
        verify { createProjectUseCase.createNewProject(validProjectName, null, null, null) }
    }

    @Test
    fun `should handle null response for description option`() {
        // Given
        val validProjectName = "Project Z"
        every { reader.getUserInput() } returnsMany listOf(validProjectName, null)

        // When
        createProjectUi.run()

        // Then
        verify { createProjectUseCase.createNewProject(validProjectName, null, null, null) }
    }

    @Test
    fun `should display success message when project creation is successful`() {
        // Given
        val validProjectName = "Successful Project"
        every { reader.getUserInput() } returnsMany listOf(validProjectName, "no")
        every {
            createProjectUseCase.createNewProject(
                any(),
                any(),
                any(),
                any()
            )
        } returns Result.success("Creation Successful")

        // When
        createProjectUi.run()

        // Then
        verify { viewer.display("Project created successfully!\n") }
    }

    @Test
    fun `should display failure message when project creation fails`() {
        // Given
        val validProjectName = "Failing Project"
        every { reader.getUserInput() } returnsMany listOf(validProjectName, "no")
        every {
            createProjectUseCase.createNewProject(
                any(),
                any(),
                any(),
                any()
            )
        } returns Result.failure(Exception("Creation Failed"))

        // When
        createProjectUi.run()

        // Then
        verify { viewer.display("Project creation failed!\n") }
    }
}
