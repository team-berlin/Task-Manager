package com.berlin.domain.usecase.task_state

import com.berlin.domain.model.Task
import com.berlin.domain.repository.TaskStateRepository
import com.berlin.domain.usecase.utils.validation.Validator
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.Test

class GetTasksByTaskStateIdUseCaseTest {

    private lateinit var getTasksByTaskStateIdUseCase: GetTasksByTaskStateIdUseCase
    private val taskStateRepository: TaskStateRepository = mockk(relaxed = true)
    private val validator: Validator = mockk(relaxed = true)

    private val task = Task(
        id = "T1",
        projectId = "P1",
        title = "Demo",
        description = null,
        stateId = "S5",
        assignedToUserId = "1",
        createByUserId = "2",
    )

    @BeforeEach
    fun setup() {
        getTasksByTaskStateIdUseCase = GetTasksByTaskStateIdUseCase(taskStateRepository,validator)
    }

    @Test
    fun `should return tasks when tasks are found for the state`() {
        // Given
        val expectedTasks = listOf(task)
        every { validator.isValid("S1") }returns true
        every { taskStateRepository.getTasksByStateId("S1") } returns expectedTasks
        every { taskStateRepository.getStateById("S1") } returns mockk()

        // When
        val result = getTasksByTaskStateIdUseCase("S1")

        // Then
        assertThat(result).isEqualTo(expectedTasks)
    }

    @Test
    fun `should throw exception when no tasks are found for the state`() {
        // Given
        every { validator.isValid("S2") }returns true
        every { taskStateRepository.getTasksByStateId("S2") } returns null
        every { taskStateRepository.getStateById("S2") } returns mockk()

        // When & Then
        val exception = assertThrows<Exception> { getTasksByTaskStateIdUseCase("S2") }
        assertThat(exception.message).isEqualTo("No tasks found for state ID S2")
    }


    @ParameterizedTest
    @ValueSource(strings = ["", " ", "123"])
    fun `should throw exception when state id is invalid`(stateId: String) {
        // When && Then
        assertThrows<Exception> {
            getTasksByTaskStateIdUseCase(stateId)
        }
    }

}