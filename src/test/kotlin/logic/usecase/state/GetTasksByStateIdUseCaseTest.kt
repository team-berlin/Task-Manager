package com.berlin.logic.usecase.state

import com.berlin.domain.usecase.state.GetTasksByStateIdUseCase
import com.berlin.domain.model.Task
import com.berlin.domain.repository.StateRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.Test

class GetTasksByStateIdUseCaseTest {

    private lateinit var getTasksByStateIdUseCase: GetTasksByStateIdUseCase
    private val stateRepository: StateRepository = mockk(relaxed = true)

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
        getTasksByStateIdUseCase = GetTasksByStateIdUseCase(stateRepository)
    }

    @Test
    fun `should return tasks when tasks are found for the state`() = runTest {
        // Given
        val expectedTasks = listOf(task)
        coEvery { stateRepository.getTasksByStateId("S1") } returns expectedTasks
        coEvery { stateRepository.getStateById("S1") } returns mockk()

        // When
        val result = getTasksByStateIdUseCase.getAllTasksByStateId("S1")

        // Then
        assertThat(result).isEqualTo(expectedTasks)
    }

    @Test
    fun `should throw exception when no tasks are found for the state`() = runTest {
        // Given
        coEvery { stateRepository.getTasksByStateId("S2") } returns null
        coEvery { stateRepository.getStateById("S2") } returns mockk()

        // When & Then
        val exception = assertThrows<Exception> { getTasksByStateIdUseCase.getAllTasksByStateId("S2") }
        assertThat(exception.message).isEqualTo("No tasks found for state ID S2")
    }

    @Test
    fun `should throw exception when state id does not exist`() = runTest {
        // Given
        coEvery { stateRepository.getStateById("S2") } returns null

        // When & Then
        val exception = assertThrows<Exception> { getTasksByStateIdUseCase.getAllTasksByStateId("S2") }
        assertThat(exception.message).isEqualTo("State with ID S2 does not exist")
    }

    @ParameterizedTest
    @ValueSource(strings = ["", " ", "123"])
    fun `should throw exception when state id is invalid`(stateId: String) = runTest {
        // When && Then
        assertThrows<Exception> {
            getTasksByStateIdUseCase.getAllTasksByStateId(stateId)
        }
    }

}