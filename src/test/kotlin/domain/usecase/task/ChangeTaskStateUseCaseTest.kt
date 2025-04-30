package com.berlin.domain.usecase.task

import com.berlin.data.memory.TaskRepositoryInMemory
import com.berlin.domain.exception.TaskNotFoundException
import com.berlin.domain.model.Task
import com.berlin.domain.model.User
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ChangeTaskStateUseCaseTest {

    private lateinit var taskRepository: TaskRepositoryInMemory
    private lateinit var useCase: ChangeTaskStateUseCase

    private val creator  = mockk<User>(relaxed = true)
    private val assignee = mockk<User>(relaxed = true)

    private val existingTask = Task(
        id          = "1",
        projectId   = "P1",
        title       = "Demo",
        description = null,
        stateId     = "TODO",
        assignedTo  = assignee,
        createBy    = creator,
        auditLogs   = emptyList()
    )

    @BeforeEach
    fun setUp() {
        taskRepository   = mockk()
        useCase = ChangeTaskStateUseCase(taskRepository)
    }


    @Test
    fun `result is success when state changes`() {
        every { taskRepository.findById("1") } returns Result.success(existingTask)
        every { taskRepository.update(any()) } answers { Result.success(firstArg()) }

        val result = useCase("1", "DONE")

        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `repository update is called with the new state id`() {
        every { taskRepository.findById("1") } returns Result.success(existingTask)
        every { taskRepository.update(any()) } answers { Result.success(firstArg()) }

        useCase("1", "DONE")

        verify(exactly = 1) {
            taskRepository.update(match { it.id == "1" && it.stateId == "DONE" })
        }
    }


    @Test
    fun `result is failure when task is not found`() {
        every { taskRepository.findById("1") } returns Result.failure(TaskNotFoundException("1"))

        val result = useCase("1", "DONE")

        assertThat(result.isFailure).isTrue()
    }


    @Test
    fun `result is failure when repository update returns unexpected error`() {
        every { taskRepository.findById("1") } returns Result.success(existingTask)
        every { taskRepository.update(any()) } returns Result.failure(IllegalStateException("boom"))

        val result = useCase("1", "DONE")

        assertThat(result.isFailure).isTrue()
    }
}
