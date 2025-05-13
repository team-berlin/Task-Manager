//package com.berlin.domain.usecase.task
//
//import com.berlin.domain.exception.InvalidAssigneeException
//import com.berlin.domain.exception.TaskNotFoundException
//import com.berlin.domain.model.AuditAction
//import com.berlin.domain.model.EntityType
//import com.berlin.domain.model.Task
//import com.berlin.domain.model.user.User
//import com.berlin.domain.model.UserRole
//import com.berlin.domain.repository.TaskRepository
//import com.berlin.domain.usecase.audit_system.AddAuditLogUseCase
//import data.UserCache
//import io.mockk.*
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import org.junit.jupiter.api.assertThrows
//import com.google.common.truth.Truth.assertThat
//
//class AssignTaskUseCaseTest {
//
//    private lateinit var taskRepository: TaskRepository
//    private lateinit var addAuditLogUseCase: AddAuditLogUseCase
//    private lateinit var userCache: UserCache
//    private lateinit var useCase: AssignTaskUseCase
//
//    private val creator = User("U0", "alice",  UserRole.ADMIN)
//    private val oldAssignee = User("U1", "john", UserRole.MATE)
//    private val anotherAssignee = User("U2", "bob", UserRole.MATE)
//
//    private val storedTask = Task(
//        id = "1",
//        projectId = "P1",
//        title = "Demo",
//        description = null,
//        stateId = "TODO",
//        assignedToUserId = oldAssignee.id,
//        createByUserId = creator.id
//    )
//
//    @BeforeEach
//    fun setUp() {
//        taskRepository = mockk(relaxed = true)
//        addAuditLogUseCase = mockk(relaxUnitFun = true)
//        userCache = mockk()
//        every { userCache.currentUser } returns creator
//
//        useCase = AssignTaskUseCase(taskRepository, addAuditLogUseCase, userCache)
//    }
//
//    @Test
//    fun `successful reassignment of task`() {
//        stubSuccessfulUpdate()
//
//        val result = useCase("1", anotherAssignee.id)
//
//        assertThat(result).isEqualTo(storedTask.copy(assignedToUserId = anotherAssignee.id))
//        verifyUpdateCall()
//        verifyAuditLog()
//    }
//
//    @Test
//    fun `throws TaskNotFoundException when task is missing`() {
//        every { taskRepository.getTaskById("1") } returns TaskNotFoundException("1")
//
//        assertThrows<TaskNotFoundException> { useCase("1", anotherAssignee.id) }
//
//        verify(exactly = 0) { taskRepository.updateTask(any()) }
//    }
//
//    @Test
//    fun `throws InvalidAssigneeException when assignee id is blank`() {
//        every { taskRepository.getTaskById("1") } returns Result.success(storedTask)
//
//        assertThrows<InvalidAssigneeException> { useCase("1", "   ") }
//
//        verify(exactly = 0) { taskRepository.updateTask(any()) }
//    }
//
//    @Test
//    fun `handles repository failure on update`() {
//        every { taskRepository.getTaskById("1") } returns Result.success(storedTask)
//        every { taskRepository.updateTask(any()) } throws IllegalStateException("Database error")
//
//        assertThrows<IllegalStateException> { useCase("1", anotherAssignee.id) }
//    }
//
//    private fun stubSuccessfulUpdate() {
//        every { taskRepository.getTaskById("1") } returns Result.success(storedTask)
//        every { taskRepository.updateTask(any()) } answers { Result.success(firstArg()) }
//        every {
//            addAuditLogUseCase.addAuditLog(
//                createdByUserId = creator.id,
//                auditAction = AuditAction.UPDATE,
//                entityType = EntityType.TASK,
//                entityId = "1"
//            )
//        } returns Result.success("audit-id-1")
//    }
//
//    private fun verifyUpdateCall() {
//        verify {
//            taskRepository.updateTask(
//                match { it.id == "1" && it.assignedToUserId == anotherAssignee.id }
//            )
//        }
//    }
//
//    private fun verifyAuditLog() {
//        verify {
//            addAuditLogUseCase.addAuditLog(
//                createdByUserId = creator.id,
//                auditAction = AuditAction.UPDATE,
//                entityType = EntityType.TASK,
//                entityId = "1"
//            )
//        }
//    }
//}
