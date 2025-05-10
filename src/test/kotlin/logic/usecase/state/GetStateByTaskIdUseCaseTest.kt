package com.berlin.logic.usecase.state

import com.berlin.domain.usecase.state.GetStateByTaskIdUseCase
import com.berlin.domain.model.State
import com.berlin.domain.repository.StateRepository
import com.berlin.domain.repository.TaskRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
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
    fun `should return state when task id exists`() = runTest {
        // Given
        val expectedState = State(id = "S1", name = "Active", projectId = "P1")
        coEvery { taskRepository.findById("T1") } returns mockk()
        coEvery { stateRepository.getStateByTaskId("T1") } returns expectedState

        // When
        val result = getStateByTaskIdUseCase.getStateByTaskId("T1")

        // Then
        assertThat(result).isEqualTo(expectedState)
    }

    @ParameterizedTest
    @ValueSource(strings = ["", " ", "123"])
    fun `should throw exception when state id is invalid`(taskId: String) = runTest {
        // When && Then
        assertThrows<Exception> {
            getStateByTaskIdUseCase.getStateByTaskId(taskId)
        }
    }

}
