package com.berlin.logic.usecase.state

import com.berlin.domain.usecase.state.GetAllStatesByProjectIdUseCase
import com.berlin.domain.model.State
import com.berlin.domain.repository.ProjectRepository
import com.berlin.domain.repository.StateRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.Test

class GetAllStatesByProjectIdUseCaseTest {

    private lateinit var getAllStatesByProjectIdUseCase: GetAllStatesByProjectIdUseCase
    private val stateRepository: StateRepository = mockk(relaxed = true)
    private val projectRepository: ProjectRepository = mockk(relaxed = true)

    @BeforeEach
    fun setup() {
        getAllStatesByProjectIdUseCase = GetAllStatesByProjectIdUseCase(
            stateRepository,
            projectRepository
        )
    }

    @Test
    fun `should return states when states are found for the project`() {
        // Given
        val expectedStates = listOf(
            State(id = "S1", name = "Active", projectId = "P1"),
            State(id = "S2", name = "Inactive", projectId = "P1")
        )
        every { projectRepository.getProjectById("P1") } returns mockk()
        every { stateRepository.getStatesByProjectId("P1") } returns expectedStates

        // When
        val result = getAllStatesByProjectIdUseCase.getAllStatesByProjectId("P1")

        // Then
        assertThat(result).isEqualTo(expectedStates)
    }

    @Test
    fun `should throw exception when no states are found for the project`() {
        // Given
        every { projectRepository.getProjectById("P1") } returns mockk()
        every { stateRepository.getStatesByProjectId("P3") } returns null

        // When & Then
        val exception = assertThrows<Exception> { getAllStatesByProjectIdUseCase.getAllStatesByProjectId("P3") }
        assertThat(exception.message).isEqualTo("No tasks found for state ID P3")
    }

    @Test
    fun `should throw exception when project ID does not exist`() {
        // Given
        every { projectRepository.getProjectById("P2") } returns null

        // When & Then
        val exception = assertThrows<Exception> { getAllStatesByProjectIdUseCase.getAllStatesByProjectId("P2") }
        assertThat(exception.message).isEqualTo("Project with ID P2 does not exist")
    }

    @ParameterizedTest
    @ValueSource(strings = ["", " ", "123"])
    fun `should throw exception when project id is invalid`(projectId: String) {
        // When && Then
        assertThrows<Exception> {
            getAllStatesByProjectIdUseCase.getAllStatesByProjectId(projectId)
        }
    }

}
