package com.berlin.domain.usecase.task

import com.berlin.domain.exception.InvalidTaskTitle
import com.berlin.domain.exception.TaskAlreadyExistsException
import com.berlin.domain.helper.IdGeneratorImplementation
import com.berlin.domain.model.Task
import com.berlin.domain.repository.TaskRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.Called
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.coVerify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CreateTaskUseCaseTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var idGenerator: IdGeneratorImplementation
    private lateinit var useCase: CreateTaskUseCase

    private val projectId = "P1"
    private val description = "opt"
    private val stateId = "TODO"
    private val createByUserId = "U1"
    private val assignedToUserId = "U2"

    @BeforeEach
    fun setUp() {
        taskRepository = mockk()
        idGenerator = mockk()
        useCase = CreateTaskUseCase(taskRepository, idGenerator)
    }

    @Test
    fun `successful creation when title valid and id unique`() = runTest {
        val rawTitle = "  Demo Task  "
        val trimmed = rawTitle.trim()
        val generated = "T123"

        coEvery { idGenerator.generateId(eq(trimmed), any(), any()) } returns generated
        // NOW: unique => no existing tasks
        coEvery { taskRepository.getAllTasks() } returns emptyList()
        coEvery { taskRepository.create(any()) }.answers { Result.success(firstArg()) }

        val result = useCase(
            projectId, rawTitle, description, stateId, createByUserId, assignedToUserId
        )

        assertThat(result.isSuccess).isTrue()
        coVerify { idGenerator.generateId(eq(trimmed), any(), any()) }
        coVerify {
            taskRepository.create(match {
                it.id == generated && it.title == trimmed
            })
        }
    }

    @Test
    fun `throws InvalidTaskTitle for blank title`() = runTest {
        assertThrows<InvalidTaskTitle> {
            useCase(
                projectId, "   ", description, stateId, createByUserId, assignedToUserId
            )
        }
        coVerify { idGenerator wasNot Called }
        coVerify { taskRepository wasNot Called }
    }

    @Test
    fun `throws InvalidTaskTitle for numeric-only title`() = runTest {
        assertThrows<InvalidTaskTitle> {
            useCase(
                projectId, "12345", description, stateId, createByUserId, assignedToUserId
            )
        }
        coVerify { idGenerator wasNot Called }
        coVerify { taskRepository wasNot Called }
    }

    @Test
    fun `throws TaskAlreadyExistsException when id is not unique`() = runTest {
        val title = "Unique"
        val generated = "T999"

        coEvery { idGenerator.generateId(eq(title), any(), any()) } returns generated
        // NOW: not unique => that ID is already in getAllTasks()
        coEvery { taskRepository.getAllTasks() } returns listOf(
            Task(generated, projectId, title, description, stateId, assignedToUserId, createByUserId)
        )

        assertThrows<TaskAlreadyExistsException> {
            useCase(
                projectId, title, description, stateId, createByUserId, assignedToUserId
            )
        }

        coVerify { idGenerator.generateId(eq(title), any(), any()) }
        coVerify(exactly = 0) { taskRepository.create(any()) }
    }

    @Test
    fun `result is failure when repository create fails`() = runTest {
        val title = "Valid"
        val generated = "T500"
        coEvery { idGenerator.generateId(eq(title), any(), any()) } returns generated
        // unique so we proceed to create
        coEvery { taskRepository.getAllTasks() } returns emptyList()
        coEvery { taskRepository.create(any()) } returns Result.failure(IllegalStateException("boom"))

        val result = useCase(
            projectId, title, description, stateId, createByUserId, assignedToUserId
        )

        assertThat(result.isFailure).isTrue()
        coVerify { taskRepository.create(match { it.id == generated }) }
    }
}
