package com.berlin.domain.usecase.state

import com.berlin.domain.exception.StateNotFoundException
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

class GetTaskStateByIdUseCaseTest {

    private lateinit var getStateByIdUseCase: GetStateByIdUseCase
    private lateinit var stateRepository: StateRepository

    @BeforeEach
    fun setup() {
        stateRepository= mockk()
        getStateByIdUseCase = GetStateByIdUseCase(stateRepository)
    }

    @Test
    fun `should return state when valid state id exists`() {
        // Given
        val expectedState = TaskState(id = "S1", name = "Active", projectId = "P1")
        every { stateRepository.getStateById("S1") } returns expectedState

        // When
        val result = getStateByIdUseCase("S1")

        // Then
        assertThat(result).isEqualTo(expectedState)
    }

    @Test
    fun `should throw exception when state id does not exist`() {
        // Given
        val input = "S2"
        every { stateRepository.getStateById(any()) } throws  StateNotFoundException(input)

        // When & Then
        assertThrows<StateNotFoundException> { getStateByIdUseCase("S2") }
    }

    @ParameterizedTest
    @ValueSource(strings = ["", " ", "123"])
    fun `should throw exception when state id is invalid`(stateId: String) {
        // When && Then
        assertThrows<Exception> {
            getStateByIdUseCase(stateId)
        }
    }

}
