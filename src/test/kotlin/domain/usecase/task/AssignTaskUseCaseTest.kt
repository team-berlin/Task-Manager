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
//import com.google.common.truth.Truth.assertThat
//import data.UserCache
//import io.mockk.*
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import org.junit.jupiter.api.assertThrows
//
//class AssignTaskUseCaseTest {
//
//    private lateinit var taskRepository: TaskRepository
//    private lateinit var addAuditLogUseCase: AddAuditLogUseCase
//    private lateinit var userCache: UserCache
//    private lateinit var useCase: AssignTaskUseCase
//
//    private val creator = User("U0", "alice", "pw", UserRole.ADMIN)
//    private val oldAssignee = User("U1", "john", "pw", UserRole.MATE)
//    private val anotherAssignee = User("U2", "bob", "pw", UserRole.MATE)
//
//    private val stored = Task(
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
//        taskRepository = mockk()
//        addAuditLogUseCase = mockk()
//        userCache = mockk()
//        every { userCache.currentUser } returns creator
//
//        useCase = AssignTaskUseCase(taskRepository, addAuditLogUseCase, userCache)
//    }
//
//    @Test
//    fun `result is success when assignee changes`() {
//        stubHappyPath()
//        val result = useCase("1", anotherAssignee.id)
//        assertThat(result.isSuccess).isTrue()
//        verifyAudit("1")
//    }
//
//    @Test
//    fun `repository update is called with new assignee`() {
//        stubHappyPath()
//        useCase("1", anotherAssignee.id)
//        verify {
//            taskRepository.updateTask(
//                match { it.id == "1" && it.assignedToUserId == anotherAssignee.id }
//            )
//        }
//    }
//
//    @Test
//    fun `result is failure when task is not found`() {
//        every { taskRepository.getTaskById("1") } returns Result.failure(TaskNotFoundException("1"))
//
//        val result = useCase("1", anotherAssignee.id)
//
//        assertThat(result.isFailure).isTrue()
//        verify(exactly = 0) { taskRepository.updateTask(any()) }
//    }
//
//    @Test
//    fun `result is failure when repository update returns unexpected error`() {
//        every { taskRepository.getTaskById("1") } returns Result.success(stored)
//        every { taskRepository.updateTask(any()) } returns Result.failure(IllegalStateException("boom"))
//
//        val result = useCase("1", anotherAssignee.id)
//
//        assertThat(result.isFailure).isTrue()
//    }
//
//    @Test
//    fun `throws InvalidAssigneeException when assignee id is blank`() {
//        every { taskRepository.getTaskById("1") } returns Result.success(stored)
//
//        assertThrows<InvalidAssigneeException> {
//            useCase("1", "   ")
//        }
//
//        verify(exactly = 0) { taskRepository.updateTask(any()) }
//    }
//
//    private fun stubHappyPath() {
//        every { taskRepository.getTaskById("1") } returns Result.success(stored)
//        every { taskRepository.updateTask(any()) } answers { Result.success(firstArg()) }
//        every {
//            addAuditLogUseCase.addAuditLog(
//                createdByUserId = creator.id,
//                auditAction = AuditAction.UPDATE,
//                changesDescription = null,
//                entityType = EntityType.TASK,
//                entityId = "1"
//            )
//        } returns Result.success("audit-id-1")
//    }
//
//    private fun verifyAudit(taskId: String) {
//        verify {
//            addAuditLogUseCase.addAuditLog(
//                createdByUserId = creator.id,
//                auditAction = AuditAction.UPDATE,
//                changesDescription = null,
//                entityType = EntityType.TASK,
//                entityId = taskId
//            )
//        }
//    }
//}
