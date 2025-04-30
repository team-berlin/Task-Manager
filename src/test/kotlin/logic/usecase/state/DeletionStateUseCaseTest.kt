package com.berlin.logic.usecase.state

import com.berlin.helper.stateHelper
import com.berlin.logic.repositories.StateRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class DeleteStateUseCaseTest {

    private lateinit var deleteStateUseCase: DeletionStateUseCase
    private val stateRepository: StateRepository = mockk(relaxed = true)

    @BeforeEach
    fun setup() {
        deleteStateUseCase = DeletionStateUseCase(stateRepository)
    }

    @Test
    fun `should return success when state deleted successfully`() {
        // Given
        every { stateRepository.deleteState(any()) } returns Result.success("")

        // When
        val result = deleteStateUseCase.deleteState("state_1")

        // Then
        assertThat(result).isEqualTo("Deleted Successfully")
    }

    @Test
    fun `should return failure when state deletion fails`() {
        // Given
        every { stateRepository.deleteState(any()) } returns Result.failure(Exception())

        // When
        val result = deleteStateUseCase.deleteState("1")

        // Then
        assertThat(result).isEqualTo(Exception("Deletion Failed"))
    }

    @Test
    fun `should return null value when state does not exist`() {
        // Given
        val input = "state_1"
        every { stateRepository.getStateById(any()) } returns null

        // When
        val exception = assertThrows<Exception> { deleteStateUseCase.deleteState("S2") }

        // Then
        assertThat(exception.message).isEqualTo(
            "State with ID $input does not exist")
    }

    @Test
    fun `should return true when state exists`() {
        // Given
        every { stateRepository.getStateById(any()) } returns mockk()

        // When
        val result = deleteStateUseCase.deleteState("S2")

        // Then
        assertThat(result).isEqualTo(true)
    }


    @ParameterizedTest
    @CsvSource("", " ", "123")
    fun `should throw exception when state ID is invalid`(stateId: String) {
        // Given
        val input = stateId

        // When && Then
        assertThrows<IllegalArgumentException> {
            deleteStateUseCase.deleteState(input)
        }
    }

    @Test
    fun `should return true when state id is valid`() {
        // Given
        val stateInput =  "state_1"

        // When
        val result = deleteStateUseCase.deleteState(stateInput)

        // Then
        assertThat(result).isEqualTo(true)
    }

}
