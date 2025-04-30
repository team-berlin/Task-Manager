package com.berlin.domain.usecase.task

import com.berlin.domain.exception.TaskNotFoundException
import com.berlin.domain.repository.TaskRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DeleteTaskUseCaseTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var deleteTaskUseCase: DeleteTaskUseCase

    @BeforeEach
    fun setUp() {
        taskRepository = mockk()
        deleteTaskUseCase = DeleteTaskUseCase(taskRepository)
    }

    @Test
    fun `result is success when repository deletes task`() {
        every { taskRepository.delete("1") } returns Result.success(Unit)

        val result = deleteTaskUseCase("1")

        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `result is failure when task is not found`() {
        every { taskRepository.delete("1") } returns Result.failure(TaskNotFoundException("1"))

        val result = deleteTaskUseCase("1")

        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `result is failure when repository returns unexpected error`() {
        every { taskRepository.delete("1") } returns Result.failure(IllegalStateException("boom"))

        val result = deleteTaskUseCase("1")

        assertThat(result.isFailure).isTrue()
    }
}
