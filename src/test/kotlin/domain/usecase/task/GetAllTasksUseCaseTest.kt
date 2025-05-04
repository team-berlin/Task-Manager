package domain.usecase.task

import com.berlin.domain.model.Task
import com.berlin.domain.repository.TaskRepository
import com.berlin.domain.usecase.task.GetAllTasksUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class GetAllTasksUseCaseTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var useCase: GetAllTasksUseCase

    private val task1 = Task(
        id = "T1",
        projectId = "P1",
        title = "First Task",
        description = "Do something",
        stateId = "TODO",
        assignedToUserId = "U1",
        createByUserId = "U2"
    )

    private val task2 = Task(
        id = "T2",
        projectId = "P2",
        title = "Second Task",
        description = null,
        stateId = "DONE",
        assignedToUserId = "U3",
        createByUserId = "U4"
    )

    @BeforeEach
    fun setUp() {
        taskRepository = mockk()
        useCase = GetAllTasksUseCase(taskRepository)
    }

    @Test
    fun `returns all tasks when repository has tasks`() {
        every { taskRepository.getAllTasks() } returns listOf(task1, task2)

        val result = useCase()

        // preserves order
        assertThat(result).containsExactly(task1, task2).inOrder()
        verify(exactly = 1) { taskRepository.getAllTasks() }
    }

    @Test
    fun `returns empty list when repository has no tasks`() {
        every { taskRepository.getAllTasks() } returns emptyList()

        val result = useCase()

        assertThat(result).isEmpty()
        verify(exactly = 1) { taskRepository.getAllTasks() }
    }

    @Test
    fun `throws exception when repository throws unexpected error`() {
        every { taskRepository.getAllTasks() } throws IllegalStateException("boom")

        assertThrows<IllegalStateException> {
            useCase()
        }
        verify(exactly = 1) { taskRepository.getAllTasks() }
    }

}
