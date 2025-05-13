//package com.berlin.domain.usecase.task
//
//import com.berlin.domain.exception.InvalidTaskTitle
//import com.berlin.domain.exception.TaskAlreadyExistsException
//
//import com.berlin.domain.model.Task
//import com.berlin.domain.repository.TaskRepository
//import com.berlin.domain.usecase.audit_system.AddAuditLogUseCase
//import com.berlin.domain.usecase.utils.id_generator.IdGenerator
//import com.google.common.truth.Truth.assertThat
//import io.mockk.every
//import io.mockk.mockk
//import io.mockk.verify
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import org.junit.jupiter.api.assertThrows
//import org.junit.jupiter.params.ParameterizedTest
//import org.junit.jupiter.params.provider.ValueSource
//
//class CreateTaskUseCaseTest {
//
//    private lateinit var taskRepository: TaskRepository
//    private lateinit var idGenerator: IdGenerator
//    private lateinit var addAuditLogUseCase: AddAuditLogUseCase
//    private lateinit var useCase: CreateTaskUseCase
//
//    private val projectId = "P1"
//    private val createByUserId = "U1"
//    private val assignedToUserId = "U2"
//
//    @BeforeEach
//    fun setUp() {
//        taskRepository = mockk(relaxed = true)
//        idGenerator = mockk(relaxed = true)
//        addAuditLogUseCase = mockk(relaxUnitFun = true)
//        useCase = CreateTaskUseCase(taskRepository, idGenerator, addAuditLogUseCase)
//    }
//
//
//    @Test
//    fun `successful creation with valid title and unique ID`() {
//        val rawTitle = "  Demo Task  "
//        val trimmed = rawTitle.trim()
//        val generatedId = "T123"
//        val task = Task(generatedId, projectId, trimmed, null, "TODO", assignedToUserId, createByUserId)
//
//        every { idGenerator.generateId(trimmed) } returns generatedId
//        every { taskRepository.getAllTasks() } returns emptyList()
//        every { taskRepository.createTask(any()) } returns task
//
//        every {
//            addAuditLogUseCase.addAuditLog(
//                createByUserId, AuditAction.CREATE,
//                null, EntityType.TASK, generatedId
//            )
//        }
//
//        val result = useCase(projectId, trimmed, null, "TODO", createByUserId, assignedToUserId)
//
//        assertThat(result).isEqualTo(task)
//        verify { idGenerator.generateId(trimmed) }
//        verify { taskRepository.createTask(any()) }
//        verify {
//            taskRepository.createTask(match {
//                it.id == generatedId && it.title == trimmed
//            })
//        }
//
//        verify {
//            addAuditLogUseCase.addAuditLog(
//                createByUserId, AuditAction.CREATE, null, EntityType.TASK, generatedId
//            )
//        }
//
//    }
//
//    @ParameterizedTest
//    @ValueSource(strings = ["", " ", "123"])
//    fun `throws InvalidTaskTitle when title is blank or numeric-only`(
//        invalidTitle: String
//    ) {
//        assertThrows<InvalidTaskTitle> {
//            useCase(projectId, invalidTitle, null, "TODO", createByUserId, assignedToUserId)
//        }
//
//        verify(exactly = 0) { idGenerator.generateId(any()) }
//        verify(exactly = 0) { taskRepository.createTask(any()) }
//    }
//
//    @Test
//    fun `throws TaskAlreadyExistsException when ID is not unique`() {
//        val title = "   Existing Task  "
//        val trimmed = title.trim()
//        val existingId = "T999"
//
//        every { idGenerator.generateId(any()) } returns existingId
//        every { taskRepository.getTaskById(existingId) } returns
//            Task(existingId, projectId, trimmed, null,
//                "TODO", assignedToUserId, createByUserId)
//
//        assertThrows<TaskAlreadyExistsException> {
//            useCase(
//                projectId, trimmed, null, "TODO", createByUserId, assignedToUserId
//            )
//        }
//
//        verify { idGenerator.generateId(trimmed) }
//        verify(exactly = 0) { taskRepository.createTask(any()) }
//    }
//
//    @Test
//    fun `handles repository failure on task creation`() {
//        val title = "Valid Task"
//        val generatedId = "T500"
//
//        every { idGenerator.generateId(title) } returns generatedId
//        every { taskRepository.getAllTasks() } returns emptyList()
//        every { taskRepository.createTask(any()) } throws IllegalStateException("Database error")
//
//        assertThrows<IllegalStateException> {
//            useCase(projectId, title, null, "TODO", createByUserId, assignedToUserId)
//        }
//
//        verify { taskRepository.createTask(any()) }
//    }
//}
//
