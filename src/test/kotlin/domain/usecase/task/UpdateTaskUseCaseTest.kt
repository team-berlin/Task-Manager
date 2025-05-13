package com.berlin.domain.usecase.task

import com.berlin.domain.exception.InvalidTaskTitle
import com.berlin.domain.exception.TaskNotFoundException
import com.berlin.domain.model.AuditLog
import com.berlin.domain.model.Task
import com.berlin.domain.model.user.User
import com.berlin.domain.repository.TaskRepository
import com.berlin.domain.usecase.audit_system.AddAuditLogUseCase
import com.google.common.truth.Truth.assertThat
import data.UserCache
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class UpdateTaskUseCaseTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var addAuditLogUseCase: AddAuditLogUseCase
    private lateinit var userCache: UserCache
    private lateinit var updateTaskUseCase: UpdateTaskUseCase

    private lateinit var creator: User

    private val stored = Task(
        id = "1",
        projectId = "P1",
        title = "Old title",
        description = "Old description",
        stateId = "TODO",
        assignedToUserId = "U2",
        createByUserId = "U1"
    )

    @BeforeEach
    fun setUp() {
        taskRepository = mockk()
        addAuditLogUseCase = mockk(relaxUnitFun = true)
        userCache = mockk()

        // current user stub
        creator = mockk(relaxed = true)
        every { creator.id } returns "U1"
        every { userCache.currentUser } returns creator

        // stub audit‐log so it never blows up
        every {
            addAuditLogUseCase(
                createdByUserId = "U1",
                auditAction = AuditLog.AuditAction.UPDATE,
                entityType = AuditLog.EntityType.TASK,
                entityId = "1"
            )
        } just Runs

        updateTaskUseCase = UpdateTaskUseCase(taskRepository, addAuditLogUseCase, userCache)
    }

    private fun primeRepoToSucceed() {
        every { taskRepository.getTaskById("1") } returns stored
        every { taskRepository.createTask(any()) } answers { firstArg() }
    }

    private fun verifyAudit() {
        verify {
            addAuditLogUseCase(
                createdByUserId = "U1",
                auditAction = AuditLog.AuditAction.UPDATE,
                entityType = AuditLog.EntityType.TASK,
                entityId = "1"
            )
        }
    }

    @Test
    fun `success when only title changes`() {
        primeRepoToSucceed()

        val result = updateTaskUseCase("1", title = "New title")

        assertThat(result.title).isEqualTo("New title")
        verifyAudit()
    }

    @Test
    fun `success when only description changes`() {
        primeRepoToSucceed()

        val result = updateTaskUseCase("1", description = "New description")

        assertThat(result.description).isEqualTo("New description")
        verifyAudit()
    }

    @Test
    fun `success when only assignee changes`() {
        primeRepoToSucceed()

        val result = updateTaskUseCase("1", assignedToUserId = "NEW_USER")

        assertThat(result.assignedToUserId).isEqualTo("NEW_USER")
        verifyAudit()
    }

    @Test
    fun `success when nothing changes (default args)`() {
        primeRepoToSucceed()

        val result = updateTaskUseCase("1")

        assertThat(result).isEqualTo(stored)
        verifyAudit()
    }

    @Test
    fun `failure when task is not found`() {
        every { taskRepository.getTaskById("1") } throws TaskNotFoundException("1")

        assertThrows<TaskNotFoundException> {
            updateTaskUseCase("1", title = "Whatever")
        }
    }

    @Test
    fun `failure when repository create returns unexpected error`() {
        every { taskRepository.getTaskById("1") } returns stored
        every { taskRepository.createTask(any()) } throws IllegalStateException("boom")

        assertThrows<IllegalStateException> {
            updateTaskUseCase("1", title = "New title")
        }
    }

    @Test
    fun `throws InvalidTaskTitle when new title is blank`() {
        primeRepoToSucceed()

        assertThrows<InvalidTaskTitle> {
            updateTaskUseCase("1", title = "   ")
        }
        verify(exactly = 0) { taskRepository.createTask(any()) }
    }

    @Test
    fun `throws InvalidTaskTitle when new title is numeric-only`() {
        primeRepoToSucceed()

        assertThrows<InvalidTaskTitle> {
            updateTaskUseCase("1", title = "123456")
        }
        verify(exactly = 0) { taskRepository.createTask(any()) }
    }
}
