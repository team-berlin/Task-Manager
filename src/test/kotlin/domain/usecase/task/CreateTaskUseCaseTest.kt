package com.berlin.domain.usecase.task

import com.berlin.domain.exception.InvalidTaskTitle
import com.berlin.domain.exception.TaskAlreadyExistsException
import com.berlin.domain.model.AuditAction
import com.berlin.domain.model.EntityType
import com.berlin.domain.model.Task
import com.berlin.domain.repository.TaskRepository
import com.berlin.domain.usecase.audit_system.AddAuditLogUseCase
import com.berlin.domain.usecase.utils.id_generator.IdGeneratorImplementation
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CreateTaskUseCaseTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var idGenerator: IdGeneratorImplementation
    private lateinit var addAuditLogUseCase: AddAuditLogUseCase
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
        addAuditLogUseCase = mockk(relaxUnitFun = true)
        useCase = CreateTaskUseCase(taskRepository, idGenerator, addAuditLogUseCase)
    }

    @Test
    fun `successful creation when title valid and id unique`() {
        val rawTitle = "  Demo Task  "
        val trimmed = rawTitle.trim()
        val generated = "T123"

        every { idGenerator.generateId(trimmed, any(), any()) } returns generated
        every { taskRepository.getAllTasks() } returns emptyList()
        every { taskRepository.createTask(any()) } answers { Result.success(firstArg()) }

        every {
            addAuditLogUseCase.addAuditLog(
                createByUserId,
                AuditAction.CREATE,
                null,
                EntityType.TASK,
                generated
            )
        } returns Result.success("log-id-123")

        val result = useCase(projectId, rawTitle, description, stateId, createByUserId, assignedToUserId)

        assertThat(result.isSuccess).isTrue()
        verify { idGenerator.generateId(trimmed, any(), any()) }
        verify {
            taskRepository.createTask(match {
                it.id == generated && it.title == trimmed
            })
        }
        verify {
            addAuditLogUseCase.addAuditLog(
                createByUserId,
                AuditAction.CREATE,
                null,
                EntityType.TASK,
                generated
            )
        }
    }


    @Test
    fun `throws InvalidTaskTitle for blank title`() {
        assertThrows<InvalidTaskTitle> {
            useCase(projectId, "   ", description, stateId, createByUserId, assignedToUserId)
        }
        verify { idGenerator wasNot Called }
        verify { taskRepository wasNot Called }
    }

    @Test
    fun `throws InvalidTaskTitle for numeric-only title`() {
        assertThrows<InvalidTaskTitle> {
            useCase(projectId, "12345", description, stateId, createByUserId, assignedToUserId)
        }
        verify { idGenerator wasNot Called }
        verify { taskRepository wasNot Called }
    }

    @Test
    fun `throws TaskAlreadyExistsException when id is not unique`() {
        val title = "Unique"
        val generated = "T999"

        every { idGenerator.generateId(title, any(), any()) } returns generated
        every { taskRepository.getAllTasks() } returns listOf(
            Task(generated, projectId, title, description, stateId, assignedToUserId, createByUserId)
        )

        assertThrows<TaskAlreadyExistsException> {
            useCase(projectId, title, description, stateId, createByUserId, assignedToUserId)
        }

        verify { idGenerator.generateId(title, any(), any()) }
        verify(exactly = 0) { taskRepository.createTask(any()) }
    }

    @Test
    fun `result is failure when repository create fails`() {
        val title = "Valid"
        val trimmed = title.trim()
        val generated = "T500"

        every { idGenerator.generateId(trimmed, any(), any()) } returns generated
        every { taskRepository.getAllTasks() } returns emptyList()
        every { taskRepository.createTask(any()) } returns Result.failure(IllegalStateException("boom"))

        val result = useCase(projectId, trimmed, description, stateId, createByUserId, assignedToUserId)

        assertThat(result.isFailure).isTrue()
        verify {
            taskRepository.createTask(match {
                it.id == generated &&
                        it.projectId == projectId &&
                        it.title == trimmed &&
                        it.assignedToUserId == assignedToUserId &&
                        it.createByUserId == createByUserId
            })
        }
    }
}
