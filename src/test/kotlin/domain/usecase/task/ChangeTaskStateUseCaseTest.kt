package com.berlin.domain.usecase.task

import com.berlin.domain.exception.InvalidTaskStateException
import com.berlin.domain.exception.TaskNotFoundException
import com.berlin.domain.model.AuditAction
import com.berlin.domain.model.EntityType
import com.berlin.domain.model.Task
import com.berlin.domain.model.User
import com.berlin.domain.repository.TaskRepository
import com.berlin.domain.usecase.auditSystem.AddAuditLogUseCase
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
    private lateinit var useCase: ChangeTaskStateUseCase

    private val creator = mockk<User>(relaxed = true)
    private val assignee = mockk<User>(relaxed = true)

    private val existingTask = Task(
        id = "1",
        projectId = "P1",
        title = "Demo",
        description = null,
        stateId = "TODO",
        assignedToUserId = assignee.id,
        createByUserId = creator.id
    )

    @BeforeEach
    fun setUp() {
        taskRepository = mockk()
        addAuditLogUseCase = mockk()
        userCache = mockk()
        every { userCache.currentUser } returns creator

        // Needed to avoid MockKException during audit logging
        every {
            addAuditLogUseCase.addAuditLog(
                createdByUserId = any(),
                auditAction = any(),
                changesDescription = any(),
                entityType = any(),
                entityId = any()
            )
        } returns Result.success("audit-log-id")

        useCase = ChangeTaskStateUseCase(taskRepository, addAuditLogUseCase, userCache)
    }

    @Test
    fun `result is success when state changes`() {
        every { taskRepository.findById("1") } returns Result.success(existingTask)
        every { taskRepository.update(any()) } answers { Result.success(firstArg()) }

        val result = useCase("1", "DONE")

        assertThat(result.isSuccess).isTrue()
        verify {
            taskRepository.update(match { it.id == "1" && it.stateId == "DONE" })
        }
        verifyAudit("1")
    }

    @Test
    fun `result is failure when task is not found`() {
        every { taskRepository.findById("1") } returns Result.failure(TaskNotFoundException("1"))

        val result = useCase("1", "DONE")

        assertThat(result.isFailure).isTrue()
        verify(exactly = 0) { taskRepository.update(any()) }
    }

    @Test
    fun `result is failure when repository update returns unexpected error`() {
        every { taskRepository.findById("1") } returns Result.success(existingTask)
        every { taskRepository.update(any()) } returns Result.failure(IllegalStateException("boom"))

        val result = useCase("1", "DONE")

        assertThat(result.isFailure).isTrue()
        verify(exactly = 1) { taskRepository.update(any()) }
    }

    @Test
    fun `throws InvalidTaskStateException when new state id is blank`() {
        every { taskRepository.findById("1") } returns Result.success(existingTask)

        assertThrows<InvalidTaskStateException> {
            useCase("1", "   ")
        }
        verify(exactly = 0) { taskRepository.update(any()) }
    }

    @Test
    fun `throws InvalidTaskStateException when new state id is numeric-only`() {
        every { taskRepository.findById("1") } returns Result.success(existingTask)

        assertThrows<InvalidTaskStateException> {
            useCase("1", "1234")
        }
        verify(exactly = 0) { taskRepository.update(any()) }
    }

    private fun verifyAudit(taskId: String) {
        verify {
            addAuditLogUseCase.addAuditLog(
                createdByUserId = creator.id,
                auditAction = AuditAction.UPDATE,
                changesDescription = null,
                entityType = EntityType.TASK,
                entityId = taskId
            )
        }
    }
}
