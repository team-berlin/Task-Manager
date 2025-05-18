package com.berlin.domain.usecase.task

import com.berlin.domain.exception.InvalidTaskStateException
import com.berlin.domain.exception.TaskNotFoundException
import com.berlin.domain.model.AuditLog
import com.berlin.domain.model.Task
import com.berlin.domain.model.user.User
import com.berlin.domain.repository.TaskRepository
import com.berlin.domain.usecase.audit_system.AddAuditLogUseCase
import com.berlin.domain.usecase.utils.validation.Validator
import com.google.common.truth.Truth.assertThat
import data.UserCache
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ChangeTaskStateUseCaseTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var addAuditLogUseCase: AddAuditLogUseCase
    private lateinit var userCache: UserCache
    private lateinit var changeTaskStateUseCase: ChangeTaskStateUseCase
    private lateinit var validator: Validator

    private val creator = mockk<User>(relaxed = true)

    private val existingTask = Task(
        id = "1",
        projectId = "P1",
        title = "Demo",
        description = null,
        stateId = "TODO",
        assignedToUserId = "U2",
        createByUserId = creator.id
    )

    @BeforeEach
    fun setUp() {
        taskRepository = mockk()
        addAuditLogUseCase = mockk(relaxUnitFun = true)
        userCache = mockk()
        validator= mockk( relaxed = true)

        every { creator.id } returns "U1"
        every { userCache.currentUser } returns creator

        // stub audit-log to just run
        every {
            addAuditLogUseCase(
                createdByUserId = "U1",
                auditAction = AuditLog.AuditAction.UPDATE,
                entityType = AuditLog.EntityType.TASK,
                entityId = any()
            )
        } just Runs

        changeTaskStateUseCase = ChangeTaskStateUseCase(taskRepository, addAuditLogUseCase, userCache,validator)
    }

    private fun verifyAudit(taskId: String) {
        verify {
            addAuditLogUseCase(
                createdByUserId = "U1",
                auditAction = AuditLog.AuditAction.UPDATE,
                entityType = AuditLog.EntityType.TASK,
                entityId = taskId
            )
        }
    }

    @Test
    fun `result is success when state changes`() {
        every { validator.isValid(any()) }returns true
        every { taskRepository.getTaskById("1") } returns existingTask
        every { taskRepository.updateTask(any()) } answers { firstArg() }

        val result = changeTaskStateUseCase("1", "DONE")

        assertThat(result.stateId).isEqualTo("DONE")
        verify {
            taskRepository.updateTask(match { it.id == "1" && it.stateId == "DONE" })
        }
        verifyAudit("1")
    }

    @Test
    fun `throws TaskNotFoundException when task is not found`() {
        every { taskRepository.getTaskById("1") } throws TaskNotFoundException("1")

        assertThrows<TaskNotFoundException> {
            changeTaskStateUseCase("1", "DONE")
        }
        verify(exactly = 0) { taskRepository.updateTask(any()) }
    }

    @Test
    fun `throws IllegalStateException when repository update fails`() {
        every { validator.isValid(any()) }returns true
        every { taskRepository.getTaskById("1") } returns existingTask
        every { taskRepository.updateTask(any()) } throws IllegalStateException("boom")

        assertThrows<IllegalStateException> {
            changeTaskStateUseCase("1", "DONE")
        }
        verify(exactly = 1) { taskRepository.updateTask(any()) }
    }

    @Test
    fun `throws InvalidTaskStateException when new state id is blank`() {
        every { taskRepository.getTaskById("1") } returns existingTask

        assertThrows<InvalidTaskStateException> {
            changeTaskStateUseCase("1", "   ")
        }
        verify(exactly = 0) { taskRepository.updateTask(any()) }
    }

    @Test
    fun `throws InvalidTaskStateException when new state id is numeric-only`() {
        every { taskRepository.getTaskById("1") } returns existingTask

        assertThrows<InvalidTaskStateException> {
            changeTaskStateUseCase("1", "1234")
        }
        verify(exactly = 0) { taskRepository.updateTask(any()) }
    }
}
