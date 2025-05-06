package presentation.state;

import com.berlin.domain.model.Project
import com.berlin.domain.usecase.state.CreateStateUseCase
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import com.berlin.presentation.state.CreateStateUi
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

private val testProject = Project(
    id = "project-123",
    name = "Test Project",
    statesId = listOf("state-1", "state-2"),
    tasksId = listOf("task-1", "task-2"),
    description = "Test project description"
)

class CreateStateUiTest {
    private lateinit var createStateUseCase: CreateStateUseCase
    private lateinit var createStateUi: CreateStateUi
    private val viewer: Viewer = mockk(relaxed = true)
    private val reader: Reader = mockk(relaxed = true)

    @BeforeEach
    fun setup() {
        createStateUseCase = mockk(relaxed = true)
        createStateUi = CreateStateUi(createStateUseCase, viewer, reader)
    }

    @Test
    fun `should display error and retry when project id is empty`() {
        // Given
        val projectId = testProject.id
        every { reader.read() } returnsMany listOf(null, projectId, "exit")

        // When
        createStateUi.run()

        // Then
        verify(exactly = 3) { reader.read() }
    }

    @Test
    fun `should display error message when state name is empty`() {
        // Given
        val projectId = testProject.id
        every { reader.read() } returnsMany listOf(projectId, null, "exit")

        // When
        createStateUi.run()

        // Then
        verify { viewer.show("State Name can not be empty") }
        verify(exactly = 3) { reader.read() }
    }

    @Test
    fun `should exit state creation when user enters exit command`() {
        // Given
        val projectId = testProject.id
        every { reader.read() } returnsMany listOf(projectId, "exit")

        // When
        createStateUi.run()

        // Then
        verify(exactly = 2) { reader.read() }
    }

    @Test
    fun `should create state successfully when valid state name is provided`() {
        // Given
        val projectId = testProject.id
        val stateName = "NewState"

        every { reader.read() } returnsMany listOf(projectId, stateName, "exit")
        every {
            createStateUseCase.createNewState(
                stateName,
                projectId
            )
        } returns Result.success("State created successfully")

        // When
        createStateUi.run()

        // Then
        verify { createStateUseCase.createNewState(stateName, projectId) }
        verify { viewer.show("State created successfully") }
    }

    @Test
    fun `should display error message when state creation fails`() {
        // Given
        val projectId = testProject.id
        val stateName = "InvalidState"

        every { reader.read() } returnsMany listOf(projectId, stateName, "exit")
        every {
            createStateUseCase.createNewState(
                stateName,
                projectId
            )
        } returns Result.failure(Exception("Creation Failed"))

        // When
        createStateUi.run()

        // Then
        verify { createStateUseCase.createNewState(stateName, projectId) }
        verify { viewer.show("Creation Failed") }
    }

    @Test
    fun `should handle exception thrown by use case and continue execution`() {
        // Given
        val projectId = testProject.id
        val stateName = " "

        every { reader.read() } returnsMany listOf(projectId, stateName, "exit")
        every {
            createStateUseCase.createNewState(
                stateName,
                projectId
            )
        } throws Exception("State Name must not be empty or blank")

        // When
        createStateUi.run()

        // Then
        verify { createStateUseCase.createNewState(stateName, projectId) }
        verify { viewer.show("Invalid State Name, Try Again") }
    }

    @Test
    fun `should handle case insensitive exit command`() {
        // Given
        val projectId = testProject.id
        every { reader.read() } returnsMany listOf(projectId, "ExIt")

        // When
        createStateUi.run()

        // Then
        verify(exactly = 2) { reader.read() }
    }

}