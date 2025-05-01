package com.berlin.logic.usecase.state

import com.berlin.logic.repositories.StateRepository
import com.berlin.model.State
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.Test

class GetStateByIdUseCaseTest {

    private lateinit var getStateByIdUseCase: GetStateByIdUseCase
    private val stateRepository: StateRepository = mockk(relaxed = true)

    @BeforeEach
    fun setup() {
        getStateByIdUseCase = GetStateByIdUseCase(stateRepository)
    }

    @Test
    fun `should return state when valid state id exists`() {
        // Given
        val expectedState = State(id = "S1", name = "Active", projectId = "P1")
        every { stateRepository.getStateById("S1") } returns expectedState

        // When
        val result = getStateByIdUseCase.getStateById("S1")

        // Then
        assertThat(result).isEqualTo(expectedState)
    }

    @Test
    fun `should throw exception when state id does not exist`() {
        // Given
        val input = "S2"
        every { stateRepository.getStateById(any()) } returns null

        // When
        val exception = assertThrows<Exception> { getStateByIdUseCase.getStateById("S2") }

        // Then
        assertThat(exception.message).isEqualTo("State with ID $input does not exist")
    }

    @ParameterizedTest
    @ValueSource(strings = ["", " ", "123"])
    fun `should throw exception when state id is invalid`(stateId: String) {
        // When && Then
        assertThrows<Exception> {
            getStateByIdUseCase.getStateById(stateId)
        }
    }

}
