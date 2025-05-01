package com.berlin.domain.usecase.task

import com.berlin.domain.exception.InvalidTaskTitle
import com.berlin.domain.exception.TaskAlreadyExistsException
import com.berlin.domain.helper.IdGeneratorImplementation
import com.berlin.domain.model.Task
import com.berlin.domain.repository.TaskRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
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
    fun `successful creation when title valid and id unique`() {
        val rawTitle = "  Demo Task  "
        val trimmed = rawTitle.trim()
        val generated = "T123"

        every { idGenerator.generateId(eq(trimmed), any(), any()) } returns generated
        // NOW: unique => no existing tasks
        every { taskRepository.getAllTasks() } returns emptyList()
        every { taskRepository.create(any()) }.answers { Result.success(firstArg()) }

        val result = useCase(
            projectId, rawTitle, description, stateId, createByUserId, assignedToUserId
        )

        assertThat(result.isSuccess).isTrue()
        verify { idGenerator.generateId(eq(trimmed), any(), any()) }
        verify {
            taskRepository.create(match {
                it.id == generated && it.title == trimmed
            })
        }
    }

    @Test
    fun `throws InvalidTaskTitle for blank title`() {
        assertThrows<InvalidTaskTitle> {
            useCase(
                projectId, "   ", description, stateId, createByUserId, assignedToUserId
            )
        }
        verify { idGenerator wasNot Called }
        verify { taskRepository wasNot Called }
    }

    @Test
    fun `throws InvalidTaskTitle for numeric-only title`() {
        assertThrows<InvalidTaskTitle> {
            useCase(
                projectId, "12345", description, stateId, createByUserId, assignedToUserId
            )
        }
        verify { idGenerator wasNot Called }
        verify { taskRepository wasNot Called }
    }

    @Test
    fun `throws TaskAlreadyExistsException when id is not unique`() {
        val title = "Unique"
        val generated = "T999"

        every { idGenerator.generateId(eq(title), any(), any()) } returns generated
        // NOW: not unique => that ID is already in getAllTasks()
        every { taskRepository.getAllTasks() } returns listOf(
            Task(generated, projectId, title, description, stateId, assignedToUserId, createByUserId)
        )

        assertThrows<TaskAlreadyExistsException> {
            useCase(
                projectId, title, description, stateId, createByUserId, assignedToUserId
            )
        }

        verify { idGenerator.generateId(eq(title), any(), any()) }
        verify(exactly = 0) { taskRepository.create(any()) }
    }

    @Test
    fun `result is failure when repository create fails`() {
        val title = "Valid"
        val generated = "T500"
        every { idGenerator.generateId(eq(title), any(), any()) } returns generated
        // unique so we proceed to create
        every { taskRepository.getAllTasks() } returns emptyList()
        every { taskRepository.create(any()) } returns Result.failure(IllegalStateException("boom"))

        val result = useCase(
            projectId, title, description, stateId, createByUserId, assignedToUserId
        )

        assertThat(result.isFailure).isTrue()
        verify { taskRepository.create(match { it.id == generated }) }
    }
}
