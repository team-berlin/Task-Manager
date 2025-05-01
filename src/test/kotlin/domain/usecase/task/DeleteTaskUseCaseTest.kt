package com.berlin.domain.usecase.task

import com.berlin.domain.exception.TaskNotFoundException
import com.berlin.domain.model.Task
import com.berlin.domain.repository.TaskRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DeleteTaskUseCaseTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var deleteTaskUseCase: DeleteTaskUseCase

    private val stored = Task(
        id = "T1",
        projectId = "P1",
        title = "Demo Task",
        description = "desc",
        stateId = "TODO",
        assignedToUserId = "U2",
        createByUserId = "U1"
    )

    @BeforeEach
    fun setUp() {
        taskRepository = mockk()
        deleteTaskUseCase = DeleteTaskUseCase(taskRepository)
    }

    @Test
    fun `result is success when repository deletes task`() {
        every { taskRepository.findById("T1") } returns Result.success(stored)
        every { taskRepository.delete("T1") } returns Result.success(Unit)

        val result = deleteTaskUseCase("T1")

        assertThat(result.isSuccess).isTrue()
        verify(exactly = 1) { taskRepository.delete("T1") }
    }

    @Test
    fun `result is failure when task is not found`() {
        every { taskRepository.findById("T1") } returns Result.failure(TaskNotFoundException("T1"))

        val result = deleteTaskUseCase("T1")

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(TaskNotFoundException::class.java)
        verify(exactly = 0) { taskRepository.delete(any()) }
    }

    @Test
    fun `result is failure when repository returns unexpected error`() {
        every { taskRepository.findById("T1") } returns Result.success(stored)
        every { taskRepository.delete("T1") } returns Result.failure(IllegalStateException("boom"))

        val result = deleteTaskUseCase("T1")

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(IllegalStateException::class.java)
        verify(exactly = 1) { taskRepository.delete("T1") }
    }

    @Test
    fun `throws Exception when id is blank`() {
        assertThrows<Exception> {
            deleteTaskUseCase("   ")
        }
        verify(exactly = 0) { taskRepository.findById(any()) }
        verify(exactly = 0) { taskRepository.delete(any()) }
    }

    @Test
    fun `throws Exception when id is numeric-only`() {
        assertThrows<Exception> {
            deleteTaskUseCase("1234")
        }
        verify(exactly = 0) { taskRepository.findById(any()) }
        verify(exactly = 0) { taskRepository.delete(any()) }
    }
}
