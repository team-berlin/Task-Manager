package com.berlin.domain.usecase.task

import com.berlin.domain.exception.InvalidTaskTitle
import com.berlin.domain.exception.TaskNotFoundException
import com.berlin.domain.model.Task
import com.berlin.domain.model.User
import com.berlin.domain.repository.TaskRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
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
    fun `success when only title changes`() {
        primeRepoToSucceed()
        val result = useCase("1", title = "New title")
        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `success when only description changes`() {
        primeRepoToSucceed()
        val result = useCase("1", description = "New description")
        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `success when only assignee changes`() {
        primeRepoToSucceed()
        val newUser = mockk<User>(relaxed = true)
        val result = useCase("1", assignedToUserId = newUser.id)
        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `success when nothing changes (default args)`() {
        primeRepoToSucceed()
        val result = useCase("1")
        assertThat(result.isSuccess).isTrue()
    }


    @Test
    fun `failure when task is not found`() {
        every { taskRepository.findById("1") } returns Result.failure(TaskNotFoundException("1"))
        val result = useCase("1", title = "Whatever")
        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `failure when repository update returns unexpected error`() {
        every { taskRepository.findById("1") } returns Result.success(stored)
        every { taskRepository.update(any()) } returns Result.failure(IllegalStateException("boom"))
        val result = useCase("1", title = "New title")
        assertThat(result.isFailure).isTrue()
    }


    @Test
    fun `throws InvalidTaskTitle when new title is blank`() {
        primeRepoToSucceed()

        assertThrows<InvalidTaskTitle> {
            useCase("1", title = "   ")
        }

        verify(exactly = 0) { taskRepository.update(any()) }
    }

    @Test
    fun `throws InvalidTaskTitle when new title is numeric-only`() {
        primeRepoToSucceed()

        assertThrows<InvalidTaskTitle> {
            useCase("1", title = "123456")
        }

        verify(exactly = 0) { taskRepository.update(any()) }
    }


    private fun primeRepoToSucceed() {
        every { taskRepository.findById("1") } returns Result.success(stored)
        every { taskRepository.update(any()) } answers { Result.success(firstArg()) }
    }
}
