package com.berlin.logic.usecase.state

import com.berlin.domain.helper.IdGeneratorImplementation
import com.berlin.domain.usecase.state.CreationStateUseCase
import com.berlin.domain.model.State
import com.berlin.domain.repository.StateRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.Test

class CreationStateUseCaseTest {

    private lateinit var createStateUseCase: CreationStateUseCase
    private val stateRepository: StateRepository = mockk(relaxed = true)

    @BeforeEach
    fun setup() {
        val idGenerator: IdGeneratorImplementation = mockk(relaxed = true)
        createStateUseCase = CreationStateUseCase(
            stateRepository,
            idGenerator
        )
    }

    @Test
    fun `createNewState should return success when state created successfully`() {
        // Given
        val validState = State(id = "S1", name = "TODO", projectId = "P1")
        every { stateRepository.addState(any()) } returns Result.success("State created successfully")
        every { stateRepository.getStateById(any()) } returns mockk()

        // When
        val result = createStateUseCase.createNewState(validState.name,
            validState.projectId)

        // Then
        assertThat(result).isEqualTo(
            Result.success("State created successfully")
        )
    }

    @Test
    fun `createNewState should return failure when state creation fails`() {
        // Given
        val validState = State(id = "S1", name = "S1", projectId = "1")
        every { stateRepository.addState(any()) } returns Result.failure(Exception())
        every { stateRepository.getStateById(any()) } returns mockk()

        // When
        val result = createStateUseCase.createNewState(validState.name,
            validState.projectId)

        // Then
        result.onFailure { exception ->
            assertThat(exception.message).isEqualTo("Creation Failed")
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ["", " ", "123"])
    fun `validateStateName should throw exception when state name is invalid`(
        invalidName: String,
    ) {
        // Given
        val stateInput = State(id = "S1", name = invalidName, projectId = "P1")

        // When && Then
        assertThrows<Exception> {
            createStateUseCase.createNewState(
                stateInput.name,
                stateInput.projectId
            )
        }
    }
}