package com.berlin.domain.usecase.task_state

import com.berlin.domain.model.TaskState
import com.berlin.domain.repository.ProjectRepository
import com.berlin.domain.repository.TaskStateRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.Test

class GetAllTaskStatesByProjectIdUseCaseTest {

    private lateinit var getAllTaskStatesByProjectIdUseCase: GetAllTaskStatesByProjectIdUseCase
    private val taskStateRepository: TaskStateRepository = mockk(relaxed = true)
    private val projectRepository: ProjectRepository = mockk(relaxed = true)

    @BeforeEach
    fun setup() {
        getAllTaskStatesByProjectIdUseCase = GetAllTaskStatesByProjectIdUseCase(
            taskStateRepository,
            projectRepository
        )
    }

    @Test
    fun `should return states when states are found for the project`() {
        // Given
        val expectedStates = listOf(
            TaskState(id = "S1", name = "Active", projectId = "P1"),
            TaskState(id = "S2", name = "Inactive", projectId = "P1")
        )
        every { projectRepository.getProjectById("P1") } returns mockk()
        every { taskStateRepository.getStatesByProjectId("P1") } returns expectedStates

        // When
        val result = getAllTaskStatesByProjectIdUseCase("P1")

        // Then
        assertThat(result).isEqualTo(expectedStates)
    }

    @Test
    fun `should return empty when no states are found for the project`() {
        // Given
        every { taskStateRepository.getStatesByProjectId("P3") } returns emptyList()
        // When
        val result = getAllTaskStatesByProjectIdUseCase("P3")
        //Then
        assertThat(result).isEmpty()
    }


    @ParameterizedTest
    @ValueSource(strings = ["", " ", "123"])
    fun `should throw exception when project id is invalid`(projectId: String) {
        // When && Then
        assertThrows<Exception> {
            getAllTaskStatesByProjectIdUseCase(projectId)
        }
    }

}
