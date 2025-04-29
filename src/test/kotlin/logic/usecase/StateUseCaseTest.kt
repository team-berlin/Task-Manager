package com.berlin.logic.usecase

import com.berlin.helper.createStateHelper
import com.berlin.logic.repositories.StateRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class StateUseCaseTest {

    private lateinit var stateUseCase: StateUseCase
    private val stateRepository: StateRepository = mockk(relaxed = true)

    @BeforeEach
    fun setup(){
        stateUseCase = StateUseCase(stateRepository)
    }

    @Test
    fun `should return true when valid state input`(){
        // Given
        val validInput = createStateHelper( name = "TODO")
        // When
        val result = stateUseCase.createNewState(validInput)
        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `should return true when state creation succeeds`(){
        // Given
        every { stateRepository.addState(any()) } returns Result.success(" ")
        // When
        val result = stateUseCase.createNewState(createStateHelper())
        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `should return false when state creation fails`(){
        // Given
        every { stateRepository.addState(any()) } returns Result.failure(Exception())
        // When
        val result = stateUseCase.createNewState(createStateHelper())
        // Then
        assertThat(result).isFalse()
    }

}
