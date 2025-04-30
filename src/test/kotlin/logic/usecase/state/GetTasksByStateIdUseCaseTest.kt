package com.berlin.logic.usecase.state

import com.berlin.logic.repositories.StateRepository
import com.berlin.logic.repositories.TaskRepository
import com.berlin.model.State
import com.berlin.model.Task
import com.berlin.model.User
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class GetTasksByStateIdUseCaseTest {

    private lateinit var getTasksByStateIdUseCase: GetTasksByStateIdUseCase
    private val stateRepository: StateRepository = mockk(relaxed = true)
    private val taskRepository: TaskRepository = mockk(relaxed = true)

    @BeforeEach
    fun setup() {
        getTasksByStateIdUseCase = GetTasksByStateIdUseCase(stateRepository, taskRepository)
    }

    @Test
    fun `should return tasks when tasks are found for the state`() {
        // Given
        every { stateRepository.getTaskByStateId("S1") } returns mockk()

        // When
        val result = getTasksByStateIdUseCase.getTasksByStateId("S1")

        // Then
        assertThat(result).isEqualTo(mockk())
    }

    @Test
    fun `should return true when state id exists`() {
        // Given
        val expectedState = State(id = "S1", name = "Active", projectId = "P1")
        every { stateRepository.getStateById("T1") } returns expectedState

        // When
        val result = getTasksByStateIdUseCase.getTasksByStateId("T1")

        // Then
        assertThat(result).isEqualTo(expectedState)
    }

    @Test
    fun `should throw exception when state id does not exist`() {
        // Given
        every { stateRepository.getStateById("S2") } returns null

        // When & Then
        val exception = assertThrows<Exception> { getTasksByStateIdUseCase.getTasksByStateId("S2") }
        assertThat(exception.message).isEqualTo("State with ID S2 does not exist")
    }

    @ParameterizedTest
    @CsvSource("", " ", "123")
    fun `should throw exception when state id is invalid`(taskId: String) {
        // Given
        val input = taskId

        // When && Then
        assertThrows<IllegalArgumentException> {
            getTasksByStateIdUseCase.getTasksByStateId(input)
        }
    }

    @Test
    fun `should return true when task id is valid`() {
        // Given
        val taskId =  "state_1"

        // When
        val result = getTasksByStateIdUseCase.getTasksByStateId(taskId)

        // Then
        assertThat(result).isEqualTo(true)
    }

    @Test
    fun `should throw exception when no tasks are found for the state`() {
        // Given
        every { stateRepository.getTaskByStateId("S3") } returns null

        // When && Then
        val exception = assertThrows<Exception> { getTasksByStateIdUseCase.getTasksByStateId("T2") }
        assertThat(exception.message).isEqualTo("State with ID T2 does not exist")
    }
}
