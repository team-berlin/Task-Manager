package com.berlin.logic.usecase.state

import com.berlin.helper.stateHelper
import com.berlin.logic.generateIdHelper.DefaultIdGenerator
import com.berlin.logic.repositories.StateRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class CreateStateUseCaseTest {

    private lateinit var createStateUseCase: CreationStateUseCase
    private val stateRepository: StateRepository = mockk(relaxed = true)


    @BeforeEach
    fun setup() {
        val idGenerator: DefaultIdGenerator = mockk()
        createStateUseCase = CreationStateUseCase(stateRepository,
            idGenerator)
    }

    @Test
    fun `createNewState should return success when state created successfully`() {
        // Given
        val validState = stateHelper(name = "TODO", projectId = "1")
        every { stateRepository.addState(any()) } returns Result.success("")

        // When
        val result = createStateUseCase.createNewState(validState.first,
            validState.second)

        // Then
        assertThat(result).isEqualTo(
            Result.success("State created successfully")
        )
    }

    @Test
    fun `createNewState should return failure when state creation fails`() {
        // Given
        val validState = stateHelper(name = "unknown",
            projectId = "1")
        every { stateRepository.addState(any()) } returns Result.failure(Exception())

        // When
        val result = createStateUseCase.createNewState(validState.first,
            validState.second)

        // Then
        assertThat(result).isEqualTo(Exception("State creation failed"))
    }

    @ParameterizedTest
    @CsvSource("", " ", "123")
    fun `validateStateName should throw exception when state name is invalid`(
        invalidName: String
    ) {
        // Given
        val stateInput = stateHelper(name = invalidName, projectId = "1")

        // When && Then
        assertThrows<IllegalArgumentException> {
            createStateUseCase.createNewState(stateInput.first,
                stateInput.second)
        }
    }

    @Test
    fun `validateStateName should return true when state name is valid`() {
        // Given
        val stateInput = stateHelper(name = "Food_1", projectId = "1")

        // When
        val result = createStateUseCase.createNewState(stateInput.first,
            stateInput.second)

        // Then
        assertThat(result).isEqualTo(true)
    }

}
