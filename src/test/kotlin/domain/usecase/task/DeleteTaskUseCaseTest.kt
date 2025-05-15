package com.berlin.domain.usecase.task

import com.berlin.domain.model.AuditLog
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

class DeleteTaskUseCaseTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var addAuditLogUseCase: AddAuditLogUseCase
    private lateinit var userCache: UserCache
    private lateinit var deleteTaskUseCase: DeleteTaskUseCase
    private lateinit var validator: Validator

    private lateinit var currentUser: User

    @BeforeEach
    fun setUp() {
        taskRepository = mockk()
        addAuditLogUseCase = mockk(relaxUnitFun = true)
        userCache = mockk()
        validator= mockk()

        currentUser = mockk(relaxed = true)
        every { currentUser.id } returns "U1"
        every { userCache.currentUser } returns currentUser

        every {
            addAuditLogUseCase(
                createdByUserId = "U1",
                auditAction = AuditLog.AuditAction.DELETE,
                entityType = AuditLog.EntityType.TASK,
                entityId = "T1"
            )
        } just Runs

        deleteTaskUseCase = DeleteTaskUseCase(taskRepository, addAuditLogUseCase, userCache,validator)
    }

    @Test
    fun `returns Deleted when repository deletes task`() {
        every { validator.isValid("T1") }returns true
        every { taskRepository.deleteTask("T1") } just Runs

        val result = deleteTaskUseCase("T1")

        assertThat(result).isEqualTo("Deleted.")
        verify { taskRepository.deleteTask("T1") }
        verify {
            addAuditLogUseCase(
                createdByUserId = "U1",
                auditAction = AuditLog.AuditAction.DELETE,
                entityType = AuditLog.EntityType.TASK,
                entityId = "T1"
            )
        }
    }

    @Test
    fun `throws IllegalStateException when repository delete throws`() {
        every { validator.isValid("T1") }returns true
        every { taskRepository.deleteTask("T1") } throws IllegalStateException("boom")

        assertThrows<IllegalStateException> {
            deleteTaskUseCase("T1")
        }

        verify { taskRepository.deleteTask("T1") }
    }

    @Test
    fun `throws Exception when id is blank`() {
        assertThrows<Exception> {
            deleteTaskUseCase("   ")
        }
        verify(exactly = 0) { taskRepository.deleteTask(any()) }
    }

    @Test
    fun `throws Exception when id is numeric-only`() {
        assertThrows<Exception> {
            deleteTaskUseCase("1234")
        }
        verify(exactly = 0) { taskRepository.deleteTask(any()) }
    }
}
