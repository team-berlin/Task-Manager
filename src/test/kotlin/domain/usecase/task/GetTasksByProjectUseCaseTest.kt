package com.berlin.domain.usecase.task

import com.berlin.domain.exception.InvalidProjectIdException
import com.berlin.domain.model.Task
import com.berlin.domain.repository.TaskRepository
import com.berlin.domain.usecase.utils.validation.Validator
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class GetTasksByProjectUseCaseTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var getTasksByProjectUseCase: GetTasksByProjectUseCase
    private lateinit var validator: Validator

    private val task = Task(
        id = "1",
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
        validator= mockk(relaxed = true)
        getTasksByProjectUseCase = GetTasksByProjectUseCase(taskRepository,validator)
    }

    @Test
    fun `returns tasks when repository returns non-empty list`() {
        every { validator.isValid("P1") }returns true
        every { taskRepository.getTasksByProjectId("P1") } returns listOf(task)

        val result = getTasksByProjectUseCase("P1")

        assertThat(result).containsExactly(task)
    }

    @Test
    fun `returns empty list when repository returns empty list`() {
        every { validator.isValid("P1") }returns true
        every { taskRepository.getTasksByProjectId("P1") } returns emptyList()

        val result = getTasksByProjectUseCase("P1")

        assertThat(result).isEmpty()
    }

    @Test
    fun `throws IllegalStateException when repository throws`() {
        every { validator.isValid("P1") }returns true
        every { taskRepository.getTasksByProjectId("P1") } throws IllegalStateException("boom")

        assertThrows<IllegalStateException> {
            getTasksByProjectUseCase("P1")
        }
    }

    @Test
    fun `throws InvalidProjectIdException when projectId is blank`() {
        assertThrows<InvalidProjectIdException> {
            getTasksByProjectUseCase("   ")
        }
        verify(exactly = 0) { taskRepository.getTasksByProjectId(any()) }
    }

    @Test
    fun `throws InvalidProjectIdException when projectId is numeric-only`() {
        assertThrows<InvalidProjectIdException> {
            getTasksByProjectUseCase("12345")
        }
        verify(exactly = 0) { taskRepository.getTasksByProjectId(any()) }
    }
}
