package com.berlin.logic.usecase.state

import com.berlin.helper.stateHelper
import com.berlin.logic.repositories.StateRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CreationStateUseCaseTest {

    private lateinit var creationStateUseCase: CreationStateUseCase
    private val stateRepository: StateRepository = mockk(relaxed = true)

    @BeforeEach
    fun setup() {
        creationStateUseCase = CreationStateUseCase(stateRepository)
    }

    @Test
    fun `createNewState should return true when state creation succeeds`() {
        // Given
        every { stateRepository.addState(any()) } returns Result.success(" ")
        // When
        val result = creationStateUseCase.createNewState(stateHelper())
        // Then
        assertThat(result).isEqualTo("Created Successfully")
    }

    @Test
    fun `createNewState should return false when state creation fails`() {
        // Given
        every { stateRepository.addState(any()) } returns Result.failure(Exception())
        // When
        val result = creationStateUseCase.createNewState(stateHelper())
        // Then
        assertThat(result).isEqualTo("Creation Failed")
    }

}