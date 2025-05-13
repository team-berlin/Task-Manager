package com.berlin.domain.usecase.task_state

import com.berlin.domain.model.TaskState
import com.berlin.domain.repository.TaskRepository
import com.berlin.domain.repository.TaskStateRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.Test

class GetTaskStateByTaskIdUseCaseTest {

    private lateinit var getTaskStateByTaskIdUseCase: GetTaskStateByTaskIdUseCase
    private val taskStateRepository: TaskStateRepository = mockk(relaxed = true)
    private val taskRepository: TaskRepository = mockk(relaxed = true)

    @BeforeEach
    fun setup() {
        getTaskStateByTaskIdUseCase = GetTaskStateByTaskIdUseCase(taskStateRepository, taskRepository)
    }

    @Test
    fun `should return state when task id exists`() {
        // Given
        val expectedState = TaskState(id = "S1", name = "Active", projectId = "P1")
        every { taskRepository.getTaskById("T1") } returns mockk()
        every { taskStateRepository.getStateByTaskId("T1") } returns expectedState

        // When
        val result = getTaskStateByTaskIdUseCase("T1")

        // Then
        assertThat(result).isEqualTo(expectedState)
    }

    @ParameterizedTest
    @ValueSource(strings = ["", " ", "123"])
    fun `should throw exception when state id is invalid`(taskId: String) {
        // When && Then
        assertThrows<Exception> {
            getTaskStateByTaskIdUseCase(taskId)
        }
    }

}
