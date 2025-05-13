package com.berlin.domain.usecase.state

import com.berlin.domain.model.TaskState
import com.berlin.domain.repository.StateRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.Test

class UpdateTaskStateUseCaseTest {

    private lateinit var updateStateUseCase: UpdateStateUseCase
    private val stateRepository: StateRepository = mockk(relaxed = true)

    @BeforeEach
    fun setup() {
        updateStateUseCase = UpdateStateUseCase(stateRepository)
    }

    @Test
    fun `should return success when state update succeeds`() {
        // Given
        val state = TaskState(id = "S1", name = "Active", projectId = "P1")
        every { stateRepository.updateState(state) } returns "Updated Successfully"

        // When
        val result = updateStateUseCase(state.id,state.name,state.projectId)

        // Then
        assertThat(result).isEqualTo("Updated Successfully")
    }

    @Test
    fun `should return exception when state update fails`() {
        // Given
        val state = TaskState(id = "S2", name = "Inactive", projectId = "P2")
        every { stateRepository.updateState(state) } throws Exception()

        // When & Then
        assertThrows<Exception> { updateStateUseCase(state.id, state.name, state.projectId) }
    }

    @ParameterizedTest
    @ValueSource(strings = ["", " ","123"])
    fun `should throw exception when state ID is invalid`(stateName: String) {
        // Given
        val input = TaskState(
            "S1",
            stateName,
            "P1"
        )

        // When && Then
        assertThrows<Exception> {
            updateStateUseCase(input.id,input.name,input.projectId)
        }
    }

}