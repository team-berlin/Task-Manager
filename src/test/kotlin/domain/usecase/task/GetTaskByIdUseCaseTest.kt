//package com.berlin.domain.usecase.task
//
//import com.berlin.domain.exception.InvalidTaskIdException
//import com.berlin.domain.model.Task
//import com.berlin.domain.repository.TaskRepository
//import com.google.common.truth.Truth.assertThat
//import io.mockk.every
//import io.mockk.mockk
//import io.mockk.verify
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import org.junit.jupiter.api.assertThrows
//
//class GetTaskByIdUseCaseTest {
//
//    private lateinit var taskRepository: TaskRepository
//    private lateinit var useCase: GetTaskByIdUseCase
//
//    private val validId = "T1"
//    private val stored = Task(
//        id = validId,
//        projectId = "P1",
//        title = "Demo",
//        description = null,
//        stateId = "TODO",
//        assignedToUserId = "U2",
//        createByUserId = "U1"
//    )
//
//    @BeforeEach
//    fun setUp() {
//        taskRepository = mockk()
//        useCase = GetTaskByIdUseCase(taskRepository)
//    }
//
//    @Test
//    fun `result is success when repository returns a task`() {
//        every { taskRepository.getTaskById(validId) } returns Result.success(stored)
//
//        val result = useCase(validId)
//
//        assertThat(result.isSuccess).isTrue()
//        assertThat(result.getOrThrow()).isEqualTo(stored)
//    }
//
//    @Test
//    fun `result is failure when repository returns failure`() {
//        val ex = IllegalStateException("boom")
//        every { taskRepository.getTaskById(validId) } returns Result.failure(ex)
//
//        val result = useCase(validId)
//
//        assertThat(result.isFailure).isTrue()
//        assertThat(result.exceptionOrNull()).isInstanceOf(IllegalStateException::class.java)
//    }
//
//    @Test
//    fun `throws InvalidTaskIdException when id is blank`() {
//        assertThrows<InvalidTaskIdException> {
//            useCase("   ")
//        }
//        verify(exactly = 0) { taskRepository.getTaskById(any()) }
//    }
//
//    @Test
//    fun `throws InvalidTaskIdException when id is numeric-only`() {
//        assertThrows<InvalidTaskIdException> {
//            useCase("1234")
//        }
//        verify(exactly = 0) { taskRepository.getTaskById(any()) }
//    }
//}
