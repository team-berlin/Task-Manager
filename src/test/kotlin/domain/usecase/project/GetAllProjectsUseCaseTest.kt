package com.berlin.domain.usecase.project

import com.berlin.domain.repository.ProjectRepository
import com.berlin.helper.projectHelper
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetAllProjectsUseCaseTest {

    private lateinit var getAllProjectsUseCase: GetAllProjectsUseCase
    private val projectRepository: ProjectRepository = mockk(relaxed = true)

    @BeforeEach
    fun setup() {
        getAllProjectsUseCase = GetAllProjectsUseCase(projectRepository)
    }

    @Test
    fun `should return list of projects when projects exist`() {
        // Given
        val expectedProjects = listOf(
            projectHelper(), projectHelper()
        )
        every { projectRepository.getAllProjects() } returns expectedProjects

        // When
        val result = getAllProjectsUseCase()

        // Then
        assertThat(result).isEqualTo(expectedProjects)
    }

    @Test
    fun `should return empty list when no projects are found`() {
        // Given
        every { projectRepository.getAllProjects() } returns emptyList()

        // When
        val result = getAllProjectsUseCase()
        // Then
        assertThat(result).isEmpty()
    }
}
