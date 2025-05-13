package com.berlin.domain.usecase.task

import com.berlin.domain.exception.InvalidTaskIdException
import com.berlin.domain.model.Task
import com.berlin.domain.repository.TaskRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class GetTaskByIdUseCaseTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var getTaskByIdUseCase: GetTaskByIdUseCase

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
        getTaskByIdUseCase = GetTaskByIdUseCase(taskRepository)
    }

    @Test
    fun `returns task when repository returns a task`() {
        // stub repo to return directly
        every { taskRepository.getTaskById(validId) } returns stored

        val result = getTaskByIdUseCase(validId)

        assertThat(result).isEqualTo(stored)
    }

    @Test
    fun `throws repository exception when repo fails`() {
        val ex = IllegalStateException("boom")
        every { taskRepository.getTaskById(validId) } throws ex

        assertThrows<IllegalStateException> {
            getTaskByIdUseCase(validId)
        }
    }

    @Test
    fun `throws InvalidTaskIdException when id is blank`() {
        assertThrows<InvalidTaskIdException> {
            getTaskByIdUseCase("   ")
        }
        // ensure we never hit the repo
        verify(exactly = 0) { taskRepository.getTaskById(any()) }
    }

    @Test
    fun `throws InvalidTaskIdException when id is numeric-only`() {
        assertThrows<InvalidTaskIdException> {
            getTaskByIdUseCase("1234")
        }
        verify(exactly = 0) { taskRepository.getTaskById(any()) }
    }
}
