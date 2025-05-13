package com.berlin.domain.usecase.task_state

import com.berlin.domain.exception.InvalidStateException
import com.berlin.domain.exception.InvalidStateNameException
import com.berlin.domain.model.TaskState
import com.berlin.domain.repository.TaskStateRepository
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

    private lateinit var createTaskStateUseCase: CreateTaskStateUseCase
    private val taskStateRepository: TaskStateRepository = mockk(relaxed = true)

    @BeforeEach
    fun setup() {
        val idGenerator: IdGeneratorImplementation = mockk(relaxed = true)
        createTaskStateUseCase = CreateTaskStateUseCase(
            taskStateRepository,
            idGenerator
        )
    }

    @Test
    fun `createNewState should return success when state created successfully`() {
        // Given
        val validState = TaskState(id = "S1", name = "TODO", projectId = "P1")
        every { taskStateRepository.addState(any()) } returns "State created successfully"
        every { taskStateRepository.getStateById(any()) } returns mockk()

        // When
        val result = createTaskStateUseCase(
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
        every { taskStateRepository.addState(any()) } throws InvalidStateException("can not add state")
        every { taskStateRepository.getStateById(any()) } returns mockk()

        // When & Then
        assertThrows<InvalidStateException> {
            createTaskStateUseCase(
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
            createTaskStateUseCase(
                stateInput.name,
                stateInput.projectId
            )
        }
    }
}
