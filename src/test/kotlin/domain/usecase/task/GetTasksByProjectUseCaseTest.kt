//package com.berlin.domain.usecase.task
//
//import com.berlin.domain.exception.InvalidProjectIdException
//import com.berlin.domain.model.Task
//import com.berlin.domain.model.user.User
//import com.berlin.domain.repository.TaskRepository
//import com.google.common.truth.Truth.assertThat
//import io.mockk.every
//import io.mockk.mockk
//import io.mockk.verify
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import org.junit.jupiter.api.assertThrows
//
//class GetTasksByProjectUseCaseTest {
//
//    private lateinit var taskRepository: TaskRepository
//    private lateinit var useCase: GetTasksByProjectUseCase
//
//    private val creator = mockk<User>(relaxed = true)
//    private val assignee = mockk<User>(relaxed = true)
//
//    private val task = Task(
//        id = "1",
//        projectId = "P1",
//        title = "Demo",
//        description = null,
//        stateId = "TODO",
//        assignedToUserId = assignee.id,
//        createByUserId = creator.id
//    )
//
//    @BeforeEach
//    fun setUp() {
//        taskRepository = mockk()
//        useCase = GetTasksByProjectUseCase(taskRepository)
//    }
//
//    @Test
//    fun `result is success when repository returns non-empty list`() {
//        every { taskRepository.getTasksByProjectId("P1") } returns Result.success(listOf(task))
//
//        val result = useCase("P1")
//
//        assertThat(result.isSuccess).isTrue()
//    }
//
//    @Test
//    fun `result is success when repository returns empty list`() {
//        every { taskRepository.getTasksByProjectId("P1") } returns Result.success(emptyList())
//
//        val result = useCase("P1")
//
//        assertThat(result.isSuccess).isTrue()
//    }
//
//    @Test
//    fun `result is failure when repository returns unexpected error`() {
//        every { taskRepository.getTasksByProjectId("P1") } returns Result.failure(IllegalStateException("boom"))
//
//        val result = useCase("P1")
//
//        assertThat(result.isFailure).isTrue()
//    }
//
//    @Test
//    fun `throws InvalidProjectIdException when projectId is blank`() {
//        assertThrows<InvalidProjectIdException> {
//            useCase("   ")
//        }
//
//        verify(exactly = 0) { taskRepository.getTasksByProjectId(any()) }
//    }
//
//    @Test
//    fun `throws InvalidProjectIdException when projectId is numeric-only`() {
//        assertThrows<InvalidProjectIdException> {
//            useCase("12345")
//        }
//
//        verify(exactly = 0) { taskRepository.getTasksByProjectId(any()) }
//    }
//}
