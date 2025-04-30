package com.berlin.domain.usecase.task

import com.berlin.domain.exception.TaskAlreadyExistsException
import com.berlin.domain.model.User
import com.berlin.domain.repository.TaskRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CreateTaskUseCaseTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var createTaskUseCase: CreateTaskUseCase
    private val creator  = mockk<User>(relaxed = true)
    private val assignee = mockk<User>(relaxed = true)

    @BeforeEach
    fun setUp() {
        taskRepository= mockk()
        createTaskUseCase = CreateTaskUseCase(taskRepository)
        every { taskRepository.nextId() } returns "1"
    }

    @Test
    fun `result is success when repository creates task`() {
        every { taskRepository.create(any()) } answers { Result.success(firstArg()) }

        val result = callUseCase()

        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `result is failure when task already exists`() {
        every { taskRepository.create(any()) } returns Result.failure(TaskAlreadyExistsException(""))

        val result = callUseCase()

        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `result is failure when repository returns unexpected error`() {
        every { taskRepository.create(any()) } returns Result.failure(IllegalStateException("boom"))

        val result = callUseCase()

        assertThat(result.isFailure).isTrue()
    }


    private fun callUseCase() = createTaskUseCase(
        projectId = "P1",
        title = "Demo Task",
        description = "optional",
        stateId = "TODO",
        creator = creator,
        assignee = assignee
    )
}
