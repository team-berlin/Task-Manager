package com.berlin.domain.usecase.task_state

import com.berlin.domain.exception.StateNotFoundException
import com.berlin.domain.model.TaskState
import com.berlin.domain.repository.TaskStateRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.Test

class GetTaskStateByIdUseCaseTest {

    private lateinit var getTaskStateByIdUseCase: GetTaskStateByIdUseCase
    private lateinit var taskStateRepository: TaskStateRepository

    @BeforeEach
    fun setup() {
        taskStateRepository = mockk()
        getTaskStateByIdUseCase = GetTaskStateByIdUseCase(taskStateRepository)
    }

    @Test
    fun `should return state when valid state id exists`() {
        // Given
        val expectedState = TaskState(id = "S1", name = "Active", projectId = "P1")
        every { taskStateRepository.getStateById("S1") } returns expectedState

        // When
        val result = getTaskStateByIdUseCase("S1")

        // Then
        assertThat(result).isEqualTo(expectedState)
    }

    @Test
    fun `should throw exception when state id does not exist`() {
        // Given
        val input = "S2"
        every { taskStateRepository.getStateById(any()) } throws StateNotFoundException(input)

        // When & Then
        assertThrows<StateNotFoundException> { getTaskStateByIdUseCase("S2") }
    }

    @ParameterizedTest
    @ValueSource(strings = ["", " ", "123"])
    fun `should throw exception when state id is invalid`(stateId: String) {
        // When && Then
        assertThrows<Exception> {
            getTaskStateByIdUseCase(stateId)
        }
    }

}
