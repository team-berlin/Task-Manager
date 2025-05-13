package com.berlin.domain.usecase.task

import com.berlin.domain.exception.InvalidAssigneeException
import com.berlin.domain.exception.TaskNotFoundException
import com.berlin.domain.model.AuditLog
import com.berlin.domain.model.Task
import com.berlin.domain.model.user.User
import com.berlin.domain.repository.TaskRepository
import com.berlin.domain.usecase.audit_system.AddAuditLogUseCase
import com.google.common.truth.Truth.assertThat
import data.UserCache
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class AssignTaskUseCaseTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var addAuditLogUseCase: AddAuditLogUseCase
    private lateinit var userCache: UserCache
    private lateinit var assignTaskUseCase: AssignTaskUseCase

    private val creator = User("U0", "alice", User.UserRole.ADMIN)
    private val oldAssignee = User("U1", "john", User.UserRole.MATE)
    private val anotherAssignee = User("U2", "bob", User.UserRole.MATE)

    private val storedTask = Task(
        id = "1",
        projectId = "P1",
        title = "Demo",
        description = null,
        stateId = "TODO",
        assignedToUserId = oldAssignee.id,
        createByUserId = creator.id
    )

    @BeforeEach
    fun setUp() {
        taskRepository = mockk(relaxed = true)
        addAuditLogUseCase = mockk(relaxUnitFun = true)
        userCache = mockk()
        every { userCache.currentUser } returns creator

        assignTaskUseCase = AssignTaskUseCase(taskRepository, addAuditLogUseCase, userCache)
    }

    @Test
    fun `successful reassignment of task`() {
        stubSuccessfulUpdate()

        val result = assignTaskUseCase("1", anotherAssignee.id)

        assertThat(result).isEqualTo(storedTask.copy(assignedToUserId = anotherAssignee.id))
        verifyUpdateCall()
        verifyAuditLog()
    }

    @Test
    fun `throws TaskNotFoundException when task is missing`() {
        every { taskRepository.getTaskById("1") } throws TaskNotFoundException("1")

        assertThrows<TaskNotFoundException> { assignTaskUseCase("1", anotherAssignee.id) }

        verify(exactly = 0) { taskRepository.updateTask(any()) }
    }

    @Test
    fun `throws InvalidAssigneeException when assignee id is blank`() {
        every { taskRepository.getTaskById("1") } returns storedTask

        assertThrows<InvalidAssigneeException> { assignTaskUseCase("1", "   ") }

        verify(exactly = 0) { taskRepository.updateTask(any()) }
    }

    @Test
    fun `handles repository failure on update`() {
        every { taskRepository.getTaskById("1") } returns storedTask
        every { taskRepository.updateTask(any()) } throws IllegalStateException("Database error")

        assertThrows<IllegalStateException> { assignTaskUseCase("1", anotherAssignee.id) }
    }

    private fun stubSuccessfulUpdate() {
        // getTaskById now returns Task directly
        every { taskRepository.getTaskById("1") } returns storedTask
        // updateTask returns the passed-in Task
        every { taskRepository.updateTask(any()) } answers { firstArg<Task>() }
        // addAuditLogUseCase is a relaxed Unit-fun, so no explicit stub needed
    }

    private fun verifyUpdateCall() {
        verify {
            taskRepository.updateTask(
                match { it.id == "1" && it.assignedToUserId == anotherAssignee.id })
        }
    }

    private fun verifyAuditLog() {
        verify {
            addAuditLogUseCase(
                createdByUserId = creator.id,
                auditAction = AuditLog.AuditAction.UPDATE,
                entityType = AuditLog.EntityType.TASK,
                entityId = "1"
            )
        }
    }
}
