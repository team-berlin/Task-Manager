//package presentation.project
//
//import com.berlin.domain.exception.InputCancelledException
//import com.berlin.domain.usecase.project.CreateProjectUseCase
//import com.berlin.presentation.io.Reader
//import com.berlin.presentation.io.Viewer
//import com.berlin.presentation.project.CreateProjectUi
//import io.mockk.*
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.params.ParameterizedTest
//import org.junit.jupiter.params.provider.ValueSource
//import kotlin.test.Test
//
//class CreateProjectUiTest {
//
//    private lateinit var createProjectUseCase: CreateProjectUseCase
//    private lateinit var viewer: Viewer
//    private lateinit var reader: Reader
//    private lateinit var ui: CreateProjectUi
//
//    @BeforeEach
//    fun setup() {
//        createProjectUseCase = mockk()
//        viewer = mockk(relaxed = true)
//        reader = mockk()
//        ui = CreateProjectUi(createProjectUseCase, viewer, reader)
//    }
//
//    @Test
//    fun `run should create project and show success message`() {
//        // Given
//        val name = "TestProject"
//        val description = "Test Description"
//
//        every { reader.read() } returns name andThen description
//        every {
//            createProjectUseCase.createNewProject(name, description, null, null)
//        } returns Result.success("Creation Successfully")
//
//        // When
//        ui.run()
//
//        // Then
//        verifySequence {
//            viewer.show("Enter project name:")
//            reader.read()
//            viewer.show("Enter project description (optional):")
//            reader.read()
//            createProjectUseCase.createNewProject(name, description, null, null)
//            viewer.show("Project created successfully")
//        }
//    }
//
//    @Test
//    fun `run should show error when project name is empty`() {
//        // Given
//        every { reader.read() } returns "   "
//
//        // When
//        ui.run()
//
//        // Then
//        verify {
//            viewer.show("Enter project name:")
//            reader.read()
//            viewer.show("Error: Project name cannot be empty")
//        }
//    }
//
//    @Test
//    fun `run should show failure message when project creation fails`() {
//        // Given
//        val name = "ValidName"
//        val description = "Something"
//
//        every { reader.read() } returns name andThen description
//        every {
//            createProjectUseCase.createNewProject(name, description, null, null)
//        } returns Result.failure(Exception("Failed"))
//
//        // When
//        ui.run()
//
//        // Then
//        verifySequence {
//            viewer.show("Enter project name:")
//            reader.read()
//            viewer.show("Enter project description (optional):")
//            reader.read()
//            createProjectUseCase.createNewProject(name, description, null, null)
//            viewer.show("Failed")
//        }
//    }
//
//    @Test
//    fun `run should show cancelled message when input is cancelled`() {
//        // Given
//        every { reader.read() } throws InputCancelledException("User cancelled")
//
//        // When
//        ui.run()
//
//        // Then
//        verify {
//            viewer.show("Enter project name:")
//            viewer.show("Project creation cancelled.")
//        }
//    }
//
//    @Test
//    fun `run should show default failure message when exception has no message`() {
//        // Given
//        val name = "ValidProject"
//        val description = "Something"
//
//        every { reader.read() } returns name andThen description
//        every {
//            createProjectUseCase.createNewProject(name, description, null, null)
//        } returns Result.failure(Exception())
//
//        // When
//        ui.run()
//
//        // Then
//        verifySequence {
//            viewer.show("Enter project name:")
//            reader.read()
//            viewer.show("Enter project description (optional):")
//            reader.read()
//            createProjectUseCase.createNewProject(name, description, null, null)
//            viewer.show("Creation failed")
//        }
//    }
//
//    @Test
//    fun `run should show error when name is null and becomes empty`() {
//        // Given
//        every { reader.read() } returns null
//
//        // When
//        ui.run()
//
//        // Then
//        verify {
//            viewer.show("Enter project name:")
//            viewer.show("Error: Project name cannot be empty")
//        }
//    }
//
//    @Test
//    fun `run should create project when description is null`() {
//        // Given
//        val name = "MyProject"
//        val nullDescription: String? = null
//
//        every { reader.read() } returns name andThen nullDescription
//        every {
//            createProjectUseCase.createNewProject(name, null, null, null)
//        } returns Result.success("Creation Successfully")
//
//        // When
//        ui.run()
//
//        // Then
//        verifySequence {
//            viewer.show("Enter project name:")
//            reader.read()
//            viewer.show("Enter project description (optional):")
//            reader.read()
//            createProjectUseCase.createNewProject(name, null, null, null)
//            viewer.show("Project created successfully")
//        }
//    }
//}
