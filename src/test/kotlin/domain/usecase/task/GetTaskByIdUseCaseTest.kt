package com.berlin.domain.usecase.task

import com.berlin.domain.exception.InvalidTaskIdException
import com.berlin.domain.model.Task
import com.berlin.domain.repository.TaskRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.coVerify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class GetTaskByIdUseCaseTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var useCase: GetTaskByIdUseCase

    private val validId = "T1"
    private val stored = Task(
        id = validId,
        projectId = "P1",
        title = "Demo",
        description = null,
        stateId = "TODO",
        assignedToUserId = "U2",
        createByUserId = "U1"
    )

    @BeforeEach
    fun setUp() {
        taskRepository = mockk()
        useCase = GetTaskByIdUseCase(taskRepository)
    }

    @Test
    fun `result is success when repository returns a task`() = runTest {
        coEvery { taskRepository.findById(validId) } returns Result.success(stored)

        val result = useCase(validId)

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrThrow()).isEqualTo(stored)
    }

    @Test
    fun `result is failure when repository returns failure`() = runTest {
        val ex = IllegalStateException("boom")
        coEvery { taskRepository.findById(validId) } returns Result.failure(ex)

        val result = useCase(validId)

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(IllegalStateException::class.java)
    }

    @Test
    fun `throws InvalidTaskIdException when id is blank`() = runTest {
        assertThrows<InvalidTaskIdException> {
            useCase("   ")
        }
        coVerify(exactly = 0) { taskRepository.findById(any()) }
    }

    @Test
    fun `throws InvalidTaskIdException when id is numeric-only`() = runTest {
        assertThrows<InvalidTaskIdException> {
            useCase("1234")
        }
        coVerify(exactly = 0) { taskRepository.findById(any()) }
    }
}
