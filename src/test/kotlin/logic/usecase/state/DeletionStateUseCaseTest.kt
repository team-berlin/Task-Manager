package com.berlin.logic.usecase.state

import com.berlin.helper.stateHelper
import com.berlin.logic.repositories.StateRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

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
        assertThat(result).isEqualTo("Deleted Successfully")
    }

    @Test
    fun `deleteState should return false when state Deletion fails`(){
        // Given
        every { stateRepository.deleteState(any()) } returns Result.failure(Exception())
        // When
        val result = deletionStateUseCase.deleteState("1")
        // Then
        assertThat(result).isEqualTo("Deletion Failed")
    }



    @Test
    fun `deleteState should throw exception when state is not exist`() {
        // Given
        every { stateRepository.deleteState(any()) } returns Result.failure(Exception())
        // When && Then
        assertThrows<NoSuchElementException> {
            deletionStateUseCase.deleteState("1")
        }
    }

    }