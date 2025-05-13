package com.berlin.domain.usecase.project

import com.berlin.domain.exception.ProjectNotFoundException
import com.berlin.domain.repository.ProjectRepository
import com.berlin.helper.projectHelper
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
        val result = getProjectByIdUseCase("P1")

        // Then
        assertThat(result).isEqualTo(expectedProject)
    }

    @Test
    fun `should throw exception when project id does not exist`() {
        // Given
        every { projectRepository.getProjectById(any()) } throws ProjectNotFoundException("")
        // When // Then
        assertThrows<ProjectNotFoundException> { getProjectByIdUseCase("P2") }
    }

    @ParameterizedTest
    @ValueSource(strings = ["", " ", "123"])
    fun `should throw exception when project id is invalid`(projectId: String) {
        // When && Then
        assertThrows<Exception> {
            getProjectByIdUseCase(projectId)
        }
    }
}