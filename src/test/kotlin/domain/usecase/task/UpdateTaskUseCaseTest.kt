package com.berlin.domain.usecase.task

import com.berlin.domain.exception.InvalidTaskTitle
import com.berlin.domain.exception.TaskNotFoundException
import com.berlin.domain.model.AuditAction
import com.berlin.domain.model.EntityType
import com.berlin.domain.model.Task
import com.berlin.domain.model.User
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
    private lateinit var useCase: UpdateTaskUseCase

    private lateinit var creator: User
    private val assignee = mockk<User>(relaxed = true)

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
        addAuditLogUseCase = mockk()
        userCache = mockk()

        creator = mockk(relaxed = true)
        every { creator.id } returns "U1"
        every { userCache.currentUser } returns creator

        // Stub audit log call to prevent MockKException
        every {
            addAuditLogUseCase.addAuditLog("U1", AuditAction.UPDATE, null, EntityType.TASK, "1")
        } returns Result.success("log-id")

        useCase = UpdateTaskUseCase(taskRepository, addAuditLogUseCase, userCache)
    }

    @Test
    fun `success when only title changes`() {
        primeRepoToSucceed()
        val result = useCase("1", title = "New title")
        assertThat(result.isSuccess).isTrue()
        verifyAudit()
    }

    @Test
    fun `success when only description changes`() {
        primeRepoToSucceed()
        val result = useCase("1", description = "New description")
        assertThat(result.isSuccess).isTrue()
        verifyAudit()
    }

    @Test
    fun `success when only assignee changes`() {
        primeRepoToSucceed()
        val result = useCase("1", assignedToUserId = "NEW_USER")
        assertThat(result.isSuccess).isTrue()
        verifyAudit()
    }

    @Test
    fun `success when nothing changes (default args)`() {
        primeRepoToSucceed()
        val result = useCase("1")
        assertThat(result.isSuccess).isTrue()
        verifyAudit()
    }

    @Test
    fun `failure when task is not found`() {
        every { taskRepository.getTaskById("1") } returns Result.failure(TaskNotFoundException("1"))
        val result = useCase("1", title = "Whatever")
        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `failure when repository create returns unexpected error`() {
        every { taskRepository.getTaskById("1") } returns Result.success(stored)
        every { taskRepository.createTask(any()) } returns Result.failure(IllegalStateException("boom"))
        val result = useCase("1", title = "New title")
        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `throws InvalidTaskTitle when new title is blank`() {
        primeRepoToSucceed()
        assertThrows<InvalidTaskTitle> {
            useCase("1", title = "   ")
        }
        verify(exactly = 0) { taskRepository.createTask(any()) }
    }

    @Test
    fun `throws InvalidTaskTitle when new title is numeric-only`() {
        primeRepoToSucceed()
        assertThrows<InvalidTaskTitle> {
            useCase("1", title = "123456")
        }
        verify(exactly = 0) { taskRepository.createTask(any()) }
    }

    private fun primeRepoToSucceed() {
        every { taskRepository.getTaskById("1") } returns Result.success(stored)
        every { taskRepository.createTask(any()) } answers { Result.success(firstArg()) }
    }

    private fun verifyAudit() {
        verify {
            addAuditLogUseCase.addAuditLog(
                createdByUserId = "U1",
                auditAction = AuditAction.UPDATE,
                changesDescription = null,
                entityType = EntityType.TASK,
                entityId = "1"
            )
        }
    }
}