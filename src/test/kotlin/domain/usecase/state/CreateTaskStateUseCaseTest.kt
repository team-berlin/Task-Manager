package com.berlin.domain.usecase.state

import com.berlin.domain.exception.InvalidStateException
import com.berlin.domain.exception.InvalidStateNameException
import com.berlin.domain.model.TaskState
import com.berlin.domain.repository.StateRepository
import com.berlin.domain.usecase.utils.id_generator.IdGeneratorImplementation
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.Test

class CreateTaskStateUseCaseTest {

    private lateinit var createStateUseCase: CreateStateUseCase
    private val stateRepository: StateRepository = mockk(relaxed = true)

    @BeforeEach
    fun setup() {
        val idGenerator: IdGeneratorImplementation = mockk(relaxed = true)
        createStateUseCase = CreateStateUseCase(
            stateRepository,
            idGenerator
        )
    }

    @Test
    fun `createNewState should return success when state created successfully`() {
        // Given
        val validState = TaskState(id = "S1", name = "TODO", projectId = "P1")
        every { stateRepository.addState(any()) } returns "State created successfully"
        every { stateRepository.getStateById(any()) } returns mockk()

        // When
        val result = createStateUseCase(
            validState.name,
            validState.projectId
        )

        // Then
        assertThat(result).isEqualTo(
            "State created successfully"
        )
    }

    @Test
    fun `createNewState should throw exception when state creation fails`() {
        // Given
        val validState = TaskState(id = "S1", name = "S1", projectId = "1")
        every { stateRepository.addState(any()) } throws InvalidStateException("can not add state")
        every { stateRepository.getStateById(any()) } returns mockk()

        // When & Then
        assertThrows<InvalidStateException> {
            createStateUseCase(
                validState.name,
                validState.projectId
            )
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ["", " ", "123"])
    fun `validateStateName should throw exception when state name is invalid`(
        invalidName: String,
    ) {
        // Given
        val stateInput = TaskState(id = "S1", name = invalidName, projectId = "P1")

        // When && Then
        assertThrows<InvalidStateNameException> {
            createStateUseCase(
                stateInput.name,
                stateInput.projectId
            )
        }
    }
}
