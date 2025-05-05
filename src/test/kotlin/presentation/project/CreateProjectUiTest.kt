package presentation.project

import com.berlin.domain.usecase.project.CreateProjectUseCase
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import com.berlin.presentation.project.CreateProjectUi
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
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
    fun `should create a project successfully when valid project name provided with no description`() {
        // Given
        val validProjectName = "My Project"
        every { reader.read() } returnsMany listOf(validProjectName, "no")

        // When
        createProjectUi.run()

        // Then
        verify { createProjectUseCase.createNewProject(validProjectName, null, null, null) }
        verify { viewer.show("Project created successfully!\n") }
    }

    @Test
    fun `should create a project successfully when user enter a description`() {
        // Given
        val validProjectName = "My Project"
        val projectDescription = "This is a test project"
        every { reader.read() } returnsMany listOf(validProjectName, "yes", projectDescription)

        // When
        createProjectUi.run()

        // Then
        verify { createProjectUseCase.createNewProject(validProjectName, projectDescription, null, null) }
        verify { viewer.show("Project created successfully!\n") }
    }

    @Test
    fun `should handle case-insensitive 'yes' for description option`() {
        // Given
        val validProjectName = "Project X"
        val projectDescription = "Description for Project X"
        every { reader.read() } returnsMany listOf(validProjectName, "YES", projectDescription)

        // When
        createProjectUi.run()

        // Then
        verify { createProjectUseCase.createNewProject(validProjectName, projectDescription, null, null) }
    }

    @Test
    fun `should pass null description when user provides anything other than 'yes'`() {
        // Given
        val validProjectName = "Another Project"
        every { reader.read() } returnsMany listOf(validProjectName, "nope")

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
        every { reader.read() } returnsMany listOf(validProjectName, "YeS", projectDescription)

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
        every { reader.read() } returnsMany listOf(validProjectName, noResponse)

        // When
        createProjectUi.run()

        // Then
        verify { createProjectUseCase.createNewProject(validProjectName, null, null, null) }
    }

    @Test
    fun `should handle null response for description option`() {
        // Given
        val validProjectName = "Project Z"
        every { reader.read() } returnsMany listOf(validProjectName, null)

        // When
        createProjectUi.run()

        // Then
        verify { createProjectUseCase.createNewProject(validProjectName, null, null, null) }
    }

    @Test
    fun `should display success message when project creation is successful`() {
        // Given
        val validProjectName = "Successful Project"
        every { reader.read() } returnsMany listOf(validProjectName, "no")
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
        verify { viewer.show("Project created successfully!\n") }
    }

    @Test
    fun `should display failure message when project creation fails`() {
        // Given
        val validProjectName = "Failing Project"
        every { reader.read() } returnsMany listOf(validProjectName, "no")
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
        verify { viewer.show("Project creation failed!\n") }
    }

    @Test
    fun `should take user input again when empty project name is provided`() {
        // Given
        every { reader.read() } returnsMany listOf("", "Valid Project Name")

        // When
        createProjectUi.run()

        // Then
        verify(exactly = 1) { viewer.show("Please enter a valid project name:") }
        verify { createProjectUseCase.createNewProject("Valid Project Name", null, null, null) }
    }

    @Test
    fun `should take user input again when blank project name is provided`() {
        // Given
        every { reader.read() } returnsMany listOf("   ", "Valid Project Name", "no")

        // When
        createProjectUi.run()

        // Then
        verify(exactly = 1) { viewer.show("Please enter a valid project name:") }
        verify { createProjectUseCase.createNewProject("Valid Project Name", null, null, null) }
    }

    @Test
    fun `should take user input again when null project name is provided`() {
        // Given
        every { reader.read() } returnsMany listOf(null, "Valid Project Name", "no")

        // When
        createProjectUi.run()

        // Then
        verify(exactly = 1) { viewer.show("Please enter a valid project name:") }
        verify { createProjectUseCase.createNewProject("Valid Project Name", null, null, null) }
    }

    @Test
    fun `should accept valid project name after multiple invalid attempts`() {
        // Given
        every { reader.read() } returnsMany listOf("", "  ", null, "Valid Project Name", "no")

        // When
        createProjectUi.run()

        // Then
        verify(exactly = 3) { viewer.show("Please enter a valid project name:") }
        verify { createProjectUseCase.createNewProject("Valid Project Name", null, null, null) }
    }
}
