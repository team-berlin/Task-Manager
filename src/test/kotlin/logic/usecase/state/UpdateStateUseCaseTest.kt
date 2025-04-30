package com.berlin.logic.usecase.state

import com.berlin.logic.repositories.StateRepository
import com.berlin.model.State
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

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
        assertThat(result).isEqualTo(Exception("Update Failed"))
    }

    @ParameterizedTest
    @CsvSource(
        "1","","S2",
        "1"," ","S2",
        "1","123","S2"
    )
    fun `should throw exception when state ID is invalid`(
        stateId: String,
        stateName: String,
        projectId: String
    ) {
        // Given
        val input = State(
            stateId,
            stateName,
            projectId
        )

        // When && Then
        assertThrows<IllegalArgumentException> {
            updateStateUseCase.updateState(input)
        }
    }

    @Test
    fun `should return true when state name is valid`() {
        // Given
        val stateInput =  State("1","TODO","S2")

        // When
        val result = updateStateUseCase.updateState(stateInput)

        // Then
        assertThat(result).isEqualTo(true)
    }

}