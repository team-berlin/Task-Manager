//package presentation.project
//
//import com.berlin.domain.exception.InvalidProjectIdException
//import com.berlin.domain.exception.ProjectNotFoundException
//import com.berlin.domain.usecase.project.GetProjectByIdUseCase
//import com.berlin.domain.model.Project
//import com.berlin.presentation.io.Reader
//import com.berlin.presentation.io.Viewer
//import com.berlin.presentation.project.GetProjectByIdUi
//import io.mockk.every
//import io.mockk.mockk
//import io.mockk.verify
//import io.mockk.verifySequence
//import org.junit.jupiter.api.BeforeEach
//import kotlin.test.Test
//
//class GetProjectByIdUiTest {
//
//    private lateinit var useCase: GetProjectByIdUseCase
//    private lateinit var viewer: Viewer
//    private lateinit var reader: Reader
//    private lateinit var ui: GetProjectByIdUi
//
//    private val project = Project(
//        id = "proj-1",
//        name = "Test Project",
//        description = "Sample description",
//        statesId = listOf("S1", "S2"),
//        tasksId = listOf("T1", "T2")
//    )
//
//    private val project2 = Project(
//        id = "proj-2",
//        name = "Test Project",
//        description = "Sample description",
//        statesId = emptyList(),
//        tasksId = emptyList()
//    )
//
//    private val project3 = Project(
//        id = "proj-3",
//        name = "Test Project",
//        description = null,
//        statesId = listOf("S1", "S2"),
//        tasksId = listOf("T1", "T2")
//    )
//
//    private val project4 = Project(
//        id = "proj-4",
//        name = "Test Project",
//        description = "Sample description",
//        statesId = listOf("S1", "S2"),
//        tasksId = null
//    )
//
//    private val project5 = Project(
//        id = "proj-5",
//        name = "Test Project",
//        description = "Sample description",
//        statesId = null,
//        tasksId = listOf("T1", "T2")
//    )
//
//    @BeforeEach
//    fun setup() {
//        useCase = mockk()
//        viewer = mockk(relaxed = true)
//        reader = mockk()
//        ui = GetProjectByIdUi(useCase, viewer, reader)
//    }
//
//    @Test
//    fun `run shows project details with states and tasks`() {
//        //Given
//        every { reader.read() } returns project.id
//        every { useCase.getProjectById(project.id) } returns project
//
//        //When
//        ui.run()
//
//        //Then
//        verifySequence {
//            viewer.show("Enter project ID:")
//            reader.read()
//            viewer.show("ID: ${project.id}")
//            viewer.show("Title: ${project.name}")
//            viewer.show("Description: ${project.description}")
//            viewer.show("States:")
//            viewer.show(" - [S1] S1")
//            viewer.show(" - [S2] S2")
//            viewer.show("\nTasks:")
//            viewer.show(" - Task ID: T1")
//            viewer.show(" - Task ID: T2")
//        }
//    }
//
//    @Test
//    fun `run shows no states or tasks when both are empty`() {
//
//        //Given
//        every { reader.read() } returns project2.id
//        every { useCase.getProjectById(project2.id) } returns project2
//
//        //When
//        ui.run()
//
//        //Then
//        verify {
//            viewer.show("No states defined for this project.")
//            viewer.show("\nNo tasks defined for this project.")
//        }
//    }
//
//    @Test
//    fun `run shows invalid project ID when InvalidProjectIdException is thrown`() {
//
//        //Given
//        every { reader.read() } returns "no-id"
//        every { useCase.getProjectById("no-id") } throws InvalidProjectIdException("Invalid project ID")
//
//        //When
//        ui.run()
//
//        //Then
//        verify { viewer.show("Invalid project ID") }
//    }
//
//    @Test
//    fun `run shows no project found when ProjectNotFoundException is thrown`() {
//
//        //Given
//        every { reader.read() } returns "proj-999"
//        every { useCase.getProjectById("proj-999") } throws ProjectNotFoundException("No project found with ID")
//
//        //When
//        ui.run()
//
//        //Then
//        verify { viewer.show("No project found with ID") }
//    }
//
//    @Test
//    fun `run shows message when unknown exception is thrown`() {
//
//        //Given
//        every { reader.read() } returns "proj-x"
//        every { useCase.getProjectById("proj-x") } throws Exception("Something went wrong")
//
//        //When
//        ui.run()
//
//        //Then
//        verify { viewer.show("Something went wrong") }
//    }
//
//    @Test
//    fun `run shows default error message when exception has no message`() {
//
//        //Given
//        every { reader.read() } returns "proj-null"
//        every { useCase.getProjectById("proj-null") } throws Exception()
//
//        //When
//        ui.run()
//
//        //Then
//        verify { viewer.show("Lookup failed") }
//    }
//
//    @Test
//    fun `run handles null input from reader and shows invalid ID`() {
//
//        //Given
//        every { reader.read() } returns null
//        every { useCase.getProjectById("") } throws InvalidProjectIdException("Invalid project ID")
//
//        //When
//        ui.run()
//
//        //Then
//        verify { viewer.show("Invalid project ID") }
//    }
//
//    @Test
//    fun `run trims input from reader and passes to use case`() {
//
//        //Given
//        every { reader.read() } returns "   proj-1  "
//        every { useCase.getProjectById("proj-1") } returns project
//
//        //When
//        ui.run()
//
//        //Then
//        verify { useCase.getProjectById("proj-1") }
//    }
//
//    @Test
//    fun `run shows (none) when project has no description`() {
//
//        //Given
//        every { reader.read() } returns project3.id
//        every { useCase.getProjectById(project3.id) } returns project3
//
//        //When
//        ui.run()
//
//        //Then
//        verify { viewer.show("Description: (none)") }
//    }
//
//    @Test
//    fun `run handles null statesId and shows no states`() {
//
//        //Given
//        every { reader.read() } returns project5.id
//        every { useCase.getProjectById(project5.id) } returns project5
//
//        //When
//        ui.run()
//
//        //Then
//        verify { viewer.show("No states defined for this project.") }
//    }
//
//    @Test
//    fun `run handles null tasksId and shows no tasks`() {
//
//        //Given
//        every { reader.read() } returns project4.id
//        every { useCase.getProjectById(project4.id) } returns project4
//
//        //When
//        ui.run()
//
//        //Then
//        verify { viewer.show("\nNo tasks defined for this project.") }
//    }
//}
//
//
//
//
