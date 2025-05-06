package com.berlin.logic.usecase.state

import com.berlin.domain.repository.StateRepository
import com.berlin.domain.usecase.state.DeleteStateUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.Test

class DeletionStateUseCaseTest {
    private lateinit var deleteStateUseCase: DeleteStateUseCase
    private val stateRepository: StateRepository = mockk(relaxed = true)

    @BeforeEach
    fun setup() {
        deleteStateUseCase = DeleteStateUseCase(stateRepository)
    }

    @Test
    fun `should return success when state is deleted successfully`() {
        // Given
        every { stateRepository.deleteState(any()) } returns Result.success("Deleted Successfully")
        every { stateRepository.getStateById(any()) } returns mockk()

        // When
        val result = deleteStateUseCase.deleteState("state_1")

        // Then
        assertThat(result).isEqualTo(Result.success("Deleted Successfully"))
    }

    @Test
    fun `should return failure when state deletion fails`() {
        // Given
        every { stateRepository.deleteState(any()) } returns Result.failure(Exception("Deletion Failed"))
        every { stateRepository.getStateById(any()) } returns mockk()

        // When
        val result = deleteStateUseCase.deleteState("state_2")

        // Then
        result.onFailure { exception ->
            assertThat(exception.message).isEqualTo("Deletion Failed")
        }
    }

    @Test
    fun `should throw exception when state does not exist`() {
        // Given
        every { stateRepository.getStateById(any()) } returns null

        // When
        val result = deleteStateUseCase.deleteState("S2")

        // Then
        result.onFailure { exception ->
            assertThat(exception.message).isEqualTo("State with ID S2 does not exist")
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ["", " ", "123"])
    fun `should throw exception when state ID is invalid`(stateId: String) {
        // When & Then
        assertThrows<Exception> {
            deleteStateUseCase.deleteState(stateId)
        }
    }
}