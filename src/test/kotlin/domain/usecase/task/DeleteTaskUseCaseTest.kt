//package com.berlin.domain.usecase.task
//
//import com.berlin.domain.exception.TaskNotFoundException
//import com.berlin.domain.model.AuditAction
//import com.berlin.domain.model.EntityType
//import com.berlin.domain.model.Task
//import com.berlin.domain.model.User
//import com.berlin.domain.repository.TaskRepository
//import com.berlin.domain.usecase.audit_system.AddAuditLogUseCase
//import com.google.common.truth.Truth.assertThat
//import data.UserCache
//import io.mockk.*
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import org.junit.jupiter.api.assertThrows
//
//class DeleteTaskUseCaseTest {
//
//    private lateinit var taskRepository: TaskRepository
//    private lateinit var addAuditLogUseCase: AddAuditLogUseCase
//    private lateinit var userCache: UserCache
//    private lateinit var deleteTaskUseCase: DeleteTaskUseCase
//
//    private lateinit var currentUser: User
//
//    private val stored = Task(
//        id = "T1",
//        projectId = "P1",
//        title = "Demo Task",
//        description = "desc",
//        stateId = "TODO",
//        assignedToUserId = "U2",
//        createByUserId = "U1"
//    )
//
//    @BeforeEach
//    fun setUp() {
//        taskRepository = mockk()
//        addAuditLogUseCase = mockk()
//        userCache = mockk()
//
//        currentUser = mockk(relaxed = true)
//        every { currentUser.id } returns "U1"
//        every { userCache.currentUser } returns currentUser
//
//        every {
//            addAuditLogUseCase.addAuditLog("U1", AuditAction.DELETE, null, EntityType.TASK, "T1")
//        } returns Result.success("audit-log-id")
//
//        deleteTaskUseCase = DeleteTaskUseCase(taskRepository, addAuditLogUseCase, userCache)
//    }
//
//    @Test
//    fun `result is success when repository deletes task`() {
//        every { taskRepository.getTaskById("T1") } returns Result.success(stored)
//        every { taskRepository.deleteTask("T1") } returns Result.success(Unit)
//
//        val result = deleteTaskUseCase("T1")
//
//        assertThat(result.isSuccess).isTrue()
//        verify { taskRepository.deleteTask("T1") }
//        verify {
//            addAuditLogUseCase.addAuditLog(
//                createdByUserId = "U1",
//                auditAction = AuditAction.DELETE,
//                changesDescription = null,
//                entityType = EntityType.TASK,
//                entityId = "T1"
//            )
//        }
//    }
//
//    @Test
//    fun `result is failure when task is not found`() {
//        every { taskRepository.getTaskById("T1") } returns Result.failure(TaskNotFoundException("T1"))
//
//        val result = deleteTaskUseCase("T1")
//
//        assertThat(result.isFailure).isTrue()
//        assertThat(result.exceptionOrNull()).isInstanceOf(TaskNotFoundException::class.java)
//        verify(exactly = 0) { taskRepository.deleteTask(any()) }
//    }
//
//    @Test
//    fun `result is failure when repository returns unexpected error`() {
//        every { taskRepository.getTaskById("T1") } returns Result.success(stored)
//        every { taskRepository.deleteTask("T1") } returns Result.failure(IllegalStateException("boom"))
//
//        val result = deleteTaskUseCase("T1")
//
//        assertThat(result.isFailure).isTrue()
//        assertThat(result.exceptionOrNull()).isInstanceOf(IllegalStateException::class.java)
//        verify { taskRepository.deleteTask("T1") }
//    }
//
//    @Test
//    fun `throws Exception when id is blank`() {
//        assertThrows<Exception> {
//            deleteTaskUseCase("   ")
//        }
//        verify(exactly = 0) { taskRepository.getTaskById(any()) }
//        verify(exactly = 0) { taskRepository.deleteTask(any()) }
//    }
//
//    @Test
//    fun `throws Exception when id is numeric-only`() {
//        assertThrows<Exception> {
//            deleteTaskUseCase("1234")
//        }
//        verify(exactly = 0) { taskRepository.getTaskById(any()) }
//        verify(exactly = 0) { taskRepository.deleteTask(any()) }
//    }
//}
