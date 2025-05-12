package com.berlin.domain.usecase.project;

import com.berlin.helper.projectHelper
import com.berlin.domain.repository.ProjectRepository
import com.berlin.domain.usecase.project.GetProjectByIdUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.Test

class GetProjectByIdUseCaseTest {

    private lateinit var getProjectByIdUseCase: GetProjectByIdUseCase
    private val projectRepository: ProjectRepository = mockk(relaxed = true)

    @BeforeEach
    fun setup() {
        getProjectByIdUseCase = GetProjectByIdUseCase(projectRepository)
    }

    @Test
    fun `should return project when valid project id exists`() {
        // Given
        val expectedProject = projectHelper()
        every { projectRepository.getProjectById("P1") } returns expectedProject

        // When
        val result = getProjectByIdUseCase.getProjectById("P1")

        // Then
        assertThat(result).isEqualTo(expectedProject)
    }

    @Test
    fun `should throw exception when project id does not exist`() {
        // Given
        val input = "P2"
        //every { projectRepository.getProjectById(any()) } returns null

        // When
        val exception = assertThrows<Exception> { getProjectByIdUseCase.getProjectById("P2") }

        // Then
        assertThat(exception.message).isEqualTo("Project with ID $input does not exist")
    }

    @ParameterizedTest
    @ValueSource(strings = ["", " ", "123"])
    fun `should throw exception when project id is invalid`(projectId: String) {
        // When && Then
        assertThrows<Exception> {
            getProjectByIdUseCase.getProjectById(projectId)
        }
    }
}