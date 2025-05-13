package com.berlin.domain.usecase.task

import com.berlin.domain.exception.InvalidTaskTitle
import com.berlin.domain.exception.TaskAlreadyExistsException
import com.berlin.domain.model.AuditLog
import com.berlin.domain.model.Task
import com.berlin.domain.repository.TaskRepository
import com.berlin.domain.usecase.audit_system.AddAuditLogUseCase
import com.berlin.domain.usecase.utils.id_generator.IdGenerator
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class CreateTaskUseCaseTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var idGenerator: IdGenerator
    private lateinit var addAuditLogUseCase: AddAuditLogUseCase
    private lateinit var createTaskUseCase: CreateTaskUseCase

    private val projectId = "P1"
    private val createByUserId = "U1"
    private val assignedToUserId = "U2"

    @BeforeEach
    fun setUp() {
        taskRepository = mockk(relaxed = true)
        idGenerator = mockk(relaxed = true)
        addAuditLogUseCase = mockk(relaxUnitFun = true)
        createTaskUseCase = CreateTaskUseCase(taskRepository, idGenerator, addAuditLogUseCase)
    }

    @Test
    fun `successful creation with valid title and unique ID`() {
        val rawTitle = "  Demo Task  "
        val trimmed = rawTitle.trim()
        val generatedId = "T123"
        val newTask = Task(
            id = generatedId,
            projectId = projectId,
            title = trimmed,
            description = null,
            stateId = "TODO",
            assignedToUserId = assignedToUserId,
            createByUserId = createByUserId
        )

        every { idGenerator.generateId(trimmed, any(), any()) } returns generatedId
        every { taskRepository.getTaskById(generatedId) } returns newTask
        every { taskRepository.createTask(any()) } returns newTask
        every {
            addAuditLogUseCase(
                createdByUserId = createByUserId,
                auditAction = AuditLog.AuditAction.CREATE,
                entityType = AuditLog.EntityType.TASK,
                entityId = generatedId,
            )
        } just Runs

        val result = createTaskUseCase(
            projectId, rawTitle, null, "TODO", createByUserId, assignedToUserId
        )

        assertThat(result).isEqualTo(newTask)

        verify { idGenerator.generateId(trimmed, any(), any()) }
        verify { taskRepository.getTaskById(generatedId) }
        verify { taskRepository.createTask(match { it.id == generatedId && it.title == trimmed }) }
        every {
            addAuditLogUseCase(
                createdByUserId = createByUserId,
                auditAction = AuditLog.AuditAction.CREATE,
                entityType = AuditLog.EntityType.TASK,
                entityId = generatedId,
            )
        } just Runs
    }

    @Test
    fun `throws TaskAlreadyExistsException when ID is not unique`() {
        val rawTitle = "   Existing Task  "
        val trimmed = rawTitle.trim()
        val existingId = "T999"

        every { idGenerator.generateId(trimmed, any(), any()) } returns existingId
        every { taskRepository.getTaskById(existingId) } throws NoSuchElementException()

        assertThrows<TaskAlreadyExistsException> {
            createTaskUseCase(
                projectId, rawTitle, null, "TODO", createByUserId, assignedToUserId
            )
        }

        verify { idGenerator.generateId(trimmed, any(), any()) }
        verify(exactly = 0) { taskRepository.createTask(any()) }
    }

    @ParameterizedTest
    @ValueSource(strings = ["", " ", "123"])
    fun `throws InvalidTaskTitle when title is blank or numeric-only`(
        invalidTitle: String,
    ) {
        assertThrows<InvalidTaskTitle> {
            createTaskUseCase(
                projectId, invalidTitle, null, "TODO", createByUserId, assignedToUserId
            )
        }

        verify(exactly = 0) { idGenerator.generateId(any(), any(), any()) }
        verify(exactly = 0) { taskRepository.createTask(any()) }
    }

    @Test
    fun `handles repository failure on task creation`() {
        val rawTitle = "Valid Task"
        val trimmed = rawTitle.trim()
        val generatedId = "T500"

        every { idGenerator.generateId(trimmed, any(), any()) } returns generatedId
        every { taskRepository.getTaskById(generatedId) } returns Task(
            generatedId, projectId, trimmed, null, "TODO", assignedToUserId, createByUserId
        )
        every { taskRepository.createTask(any()) } throws IllegalStateException("Database error")

        assertThrows<IllegalStateException> {
            createTaskUseCase(
                projectId, rawTitle, null, "TODO", createByUserId, assignedToUserId
            )
        }
        verify { taskRepository.createTask(any()) }
    }
}
