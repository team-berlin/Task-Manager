package com.berlin.domain.usecase.task_state

import com.berlin.domain.model.TaskState
import com.berlin.domain.repository.TaskStateRepository
import com.berlin.domain.usecase.utils.validation.Validator
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.Test

class UpdateTaskStateUseCaseTest {

    private lateinit var updateTaskStateUseCase: UpdateTaskStateUseCase
    private val taskStateRepository: TaskStateRepository = mockk(relaxed = true)
    private val validator: Validator = mockk(relaxed = true)

    @BeforeEach
    fun setup() {
        updateTaskStateUseCase = UpdateTaskStateUseCase(taskStateRepository,validator)
    }

    @Test
    fun `should return success when state update succeeds`() {
        // Given
        val state = TaskState(id = "S1", name = "Active", projectId = "P1")
        every { validator.isValid(any()) }returns true
        every { taskStateRepository.updateState(state) } returns "Updated Successfully"

        // When
        val result = updateTaskStateUseCase(state.id, state.name, state.projectId)

        // Then
        assertThat(result).isEqualTo("Updated Successfully")
    }

    @Test
    fun `should return exception when state update fails`() {
        // Given
        val state = TaskState(id = "S2", name = "Inactive", projectId = "P2")
        every { validator.isValid(any()) }returns true
        every { taskStateRepository.updateState(state) } throws Exception()

        // When & Then
        assertThrows<Exception> { updateTaskStateUseCase(state.id, state.name, state.projectId) }
    }

    @ParameterizedTest
    @ValueSource(strings = ["", " ", "123"])
    fun `should throw exception when state ID is invalid`(stateName: String) {
        // Given
        val input = TaskState(
            "S1",
            stateName,
            "P1"
        )

        // When && Then
        assertThrows<Exception> {
            updateTaskStateUseCase(input.id, input.name, input.projectId)
        }
    }

}