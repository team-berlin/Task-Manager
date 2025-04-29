package com.berlin.logic.usecase.state

import com.berlin.logic.repositories.StateRepository
import com.google.common.truth.Truth
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DeletionStateUseCaseTest {

    private lateinit var deletionStateUseCase: DeletionStateUseCase
    private val stateRepository: StateRepository = mockk(relaxed = true)

    @BeforeEach
    fun setup(){
        deletionStateUseCase = DeletionStateUseCase(stateRepository)
    }

    @Test
    fun `deleteState should return true when state Deletion succeeds`(){
        // Given
        every { stateRepository.deleteState(any()) } returns Result.success(" ")
        // When
        val result = deletionStateUseCase.deleteState("1")
        // Then
        Truth.assertThat(result).isEqualTo("Deleted Success")
    }

    @Test
    fun `deleteState should return false when state Deletion fails`(){
        // Given
        every { stateRepository.deleteState(any()) } returns Result.failure(Exception())
        // When
        val result = deletionStateUseCase.deleteState("1")
        // Then
        Truth.assertThat(result).isEqualTo("Deleted Success")
    }

}