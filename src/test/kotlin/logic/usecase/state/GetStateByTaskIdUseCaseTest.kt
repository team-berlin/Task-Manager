package com.berlin.logic.usecase.state

import com.berlin.domain.usecase.state.GetStateByTaskIdUseCase
import com.berlin.domain.model.State
import com.berlin.domain.repository.StateRepository
import com.berlin.domain.repository.TaskRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.Test

class GetStateByTaskIdUseCaseTest {

    private lateinit var getStateByTaskIdUseCase: GetStateByTaskIdUseCase
    private val stateRepository: StateRepository = mockk(relaxed = true)
    private val taskRepository: TaskRepository = mockk(relaxed = true)

    @BeforeEach
    fun setup() {
        getStateByTaskIdUseCase = GetStateByTaskIdUseCase(stateRepository, taskRepository)
    }

    @Test
    fun `should return state when task id exists`() {
        // Given
        val expectedState = State(id = "S1", name = "Active", projectId = "P1")
        every { taskRepository.findById("T1") } returns mockk()
        every { stateRepository.getStateByTaskId("T1") } returns expectedState

        // When
        val result = getStateByTaskIdUseCase.getStateByTaskId("T1")

        // Then
        assertThat(result).isEqualTo(expectedState)
    }

//    @Test
//    fun `should throw exception when task id does not exist`() {
//        // Given
//        every { taskRepository.findById("T2") } returns Result.failure(Exception("State with ID T2 does not exist"))
//
//        // When & Then
//        val exception = assertThrows<Exception> { getStateByTaskIdUseCase.getStateByTaskId("T2") }
//        assertThat(exception.message).isEqualTo("State with ID T2 does not exist")
//    }

    @ParameterizedTest
    @ValueSource(strings = ["", " ", "123"])
    fun `should throw exception when state id is invalid`(taskId: String) {
        // When && Then
        assertThrows<Exception> {
            getStateByTaskIdUseCase.getStateByTaskId(taskId)
        }
    }

}
