package com.berlin.domain.usecase.task

import com.berlin.domain.exception.InvalidTaskTitle
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

class UpdateTaskUseCaseTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var useCase: UpdateTaskUseCase

    private val creator = mockk<User>(relaxed = true)
    private val assignee = mockk<User>(relaxed = true)

    private val stored = Task(
        id = "1",
        projectId = "P1",
        title = "Old title",
        description = "Old description",
        stateId = "TODO",
        assignedToUserId = assignee.id,
        createByUserId = creator.id
    )

    @BeforeEach
    fun setUp() {
        taskRepository = mockk()
        useCase = UpdateTaskUseCase(taskRepository)
    }

    @Test
    fun `success when only title changes`() = runTest {
        primeRepoToSucceed()
        val result = useCase("1", title = "New title")
        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `success when only description changes`() = runTest {
        primeRepoToSucceed()
        val result = useCase("1", description = "New description")
        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `success when only assignee changes`() = runTest {
        primeRepoToSucceed()
        val newUser = mockk<User>(relaxed = true)
        val result = useCase("1", assignedToUserId = newUser.id)
        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `success when nothing changes (default args)`() = runTest {
        primeRepoToSucceed()
        val result = useCase("1")
        assertThat(result.isSuccess).isTrue()
    }


    @Test
    fun `failure when task is not found`() = runTest {
        coEvery { taskRepository.findById("1") } returns Result.failure(TaskNotFoundException("1"))
        val result = useCase("1", title = "Whatever")
        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `failure when repository update returns unexpected error`() = runTest {
        coEvery { taskRepository.findById("1") } returns Result.success(stored)
        coEvery { taskRepository.update(any()) } returns Result.failure(IllegalStateException("boom"))
        val result = useCase("1", title = "New title")
        assertThat(result.isFailure).isTrue()
    }


    @Test
    fun `throws InvalidTaskTitle when new title is blank`() = runTest {
        primeRepoToSucceed()

        assertThrows<InvalidTaskTitle> {
            useCase("1", title = "   ")
        }

        coVerify(exactly = 0) { taskRepository.update(any()) }
    }

    @Test
    fun `throws InvalidTaskTitle when new title is numeric-only`() = runTest {
        primeRepoToSucceed()

        assertThrows<InvalidTaskTitle> {
            useCase("1", title = "123456")
        }

        coVerify(exactly = 0) { taskRepository.update(any()) }
    }


    private fun primeRepoToSucceed() {
        coEvery { taskRepository.findById("1") } returns Result.success(stored)
        coEvery { taskRepository.update(any()) } answers { Result.success(firstArg()) }
    }
}
