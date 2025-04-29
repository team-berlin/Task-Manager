package com.berlin.logic.usecase.state

import com.berlin.helper.stateHelper
import com.berlin.logic.repositories.StateRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class UpdateStateUseCaseTest {

    private lateinit var updateStateUseCase: UpdateStateUseCase
    private val stateRepository: StateRepository = mockk(relaxed = true)

    @BeforeEach
    fun setup(){
        updateStateUseCase = UpdateStateUseCase(stateRepository)
    }

    @Test
    fun `updateState should return true when state update succeeds`(){
        // Given
        every { stateRepository.updateState(any()) } returns Result.success(" ")
        // When
        val result = updateStateUseCase.updateState(stateHelper())
        // Then
        assertThat(result).isEqualTo("Updated Successfully")
    }

    @Test
    fun `updateState should return false when state update fails`(){
        // Given
        every { stateRepository.updateState(any()) } returns Result.failure(Exception())
        // When
        val result = updateStateUseCase.updateState(stateHelper())
        // Then
        assertThat(result).isEqualTo("Update Failed")
    }


    @Test
    fun `updateState should throw exception when state is not exist`(){
        // Given
        every { stateRepository.updateState(any()) } returns Result.failure(Exception())
        // When && Then
        assertThrows<NoSuchElementException> {
            updateStateUseCase.updateState(stateHelper())
        }

    }


 }
