package com.berlin.domain.usecase.task

import com.berlin.domain.exception.InvalidTaskStateException
import com.berlin.domain.exception.TaskNotFoundException
import com.berlin.domain.model.Task
import com.berlin.domain.model.User
import com.berlin.domain.repository.TaskRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.coVerify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ChangeTaskStateUseCaseTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var useCase: ChangeTaskStateUseCase

    private val creator = mockk<User>(relaxed = true)
    private val assignee = mockk<User>(relaxed = true)

    private val existingTask = Task(
        id = "1",
        projectId = "P1",
        title = "Demo",
        description = null,
        stateId = "TODO",
        assignedToUserId = assignee.id,
        createByUserId = creator.id
    )

    @BeforeEach
    fun setUp() {
        taskRepository = mockk()
        useCase = ChangeTaskStateUseCase(taskRepository)
    }

    @Test
    fun `result is success when state changes`() = runTest {
        coEvery { taskRepository.findById("1") } returns Result.success(existingTask)
        coEvery { taskRepository.update(any()) } answers { Result.success(firstArg()) }

        val result = useCase("1", "DONE")

        assertThat(result.isSuccess).isTrue()
        coVerify(exactly = 1) {
            taskRepository.update(match { it.id == "1" && it.stateId == "DONE" })
        }
    }

    @Test
    fun `result is failure when task is not found`() = runTest {
        coEvery { taskRepository.findById("1") } returns Result.failure(TaskNotFoundException("1"))

        val result = useCase("1", "DONE")

        assertThat(result.isFailure).isTrue()
        coVerify(exactly = 0) { taskRepository.update(any()) }
    }

    @Test
    fun `result is failure when repository update returns unexpected error`() = runTest {
        coEvery { taskRepository.findById("1") } returns Result.success(existingTask)
        coEvery { taskRepository.update(any()) } returns Result.failure(IllegalStateException("boom"))

        val result = useCase("1", "DONE")

        assertThat(result.isFailure).isTrue()
        coVerify(exactly = 1) { taskRepository.update(any()) }
    }

    @Test
    fun `throws InvalidTaskStateException when new state id is blank`() = runTest {
        coEvery { taskRepository.findById("1") } returns Result.success(existingTask)

        assertThrows<InvalidTaskStateException> {
            useCase("1", "   ")
        }
        // no update should be attempted after validation fails
        coVerify(exactly = 0) { taskRepository.update(any()) }
    }

    @Test
    fun `throws InvalidTaskStateException when new state id is numeric-only`() = runTest {
        coEvery { taskRepository.findById("1") } returns Result.success(existingTask)

        assertThrows<InvalidTaskStateException> {
            useCase("1", "1234")
        }
        coVerify(exactly = 0) { taskRepository.update(any()) }
    }
}
