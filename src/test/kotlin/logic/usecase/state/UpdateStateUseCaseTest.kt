package com.berlin.logic.usecase.state

import com.berlin.domain.usecase.state.UpdateStateUseCase
import com.berlin.domain.model.State
import com.berlin.domain.repository.StateRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.Test

class UpdateStateUseCaseTest {

    private lateinit var updateStateUseCase: UpdateStateUseCase
    private val stateRepository: StateRepository = mockk(relaxed = true)

    @BeforeEach
    fun setup() {
        updateStateUseCase = UpdateStateUseCase(stateRepository)
    }

    @Test
    fun `should return success when state update succeeds`() {
        // Given
        val state = State(id = "S1", name = "Active", projectId = "P1")
        every { stateRepository.updateState(state) } returns Result.success("Updated Successfully")

        // When
        val result = updateStateUseCase.updateState(state)

        // Then
        assertThat(result).isEqualTo(Result.success("Updated Successfully"))
    }

    @Test
    fun `should return failure when state update fails`() {
        // Given
        val state = State(id = "S2", name = "Inactive", projectId = "P2")
        every { stateRepository.updateState(state) } returns Result.failure(Exception())

        // When
        val result = updateStateUseCase.updateState(state)

        // Then
        result.onFailure { exception ->
            assertThat(exception.message).isEqualTo("Update Failed")
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ["", " ","123"])
    fun `should throw exception when state ID is invalid`(stateName: String) {
        // Given
        val input = State(
            "S1",
            stateName,
            "P1"
        )

        // When && Then
        assertThrows<Exception> {
            updateStateUseCase.updateState(input)
        }
    }

}