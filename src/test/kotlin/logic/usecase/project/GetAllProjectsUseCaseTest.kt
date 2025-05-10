package com.berlin.logic.usecase.project

import com.berlin.helper.projectHelper
import com.berlin.domain.repository.ProjectRepository
import com.berlin.domain.usecase.project.GetAllProjectsUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class GetAllProjectsUseCaseTest {

    private lateinit var getAllProjectsUseCase: GetAllProjectsUseCase
    private val projectRepository: ProjectRepository = mockk(relaxed = true)

    @BeforeEach
    fun setup() {
        getAllProjectsUseCase = GetAllProjectsUseCase(projectRepository)
    }

    @Test
    fun `should return list of projects when projects exist`() = runTest {
        // Given
        val expectedProjects = listOf(
            projectHelper(),
            projectHelper()
        )
        coEvery { projectRepository.getAllProjects() } returns expectedProjects

        // When
        val result = getAllProjectsUseCase.getAllProjects()

        // Then
        assertThat(result).isEqualTo(expectedProjects)
    }

    @Test
    fun `should throw exception when no projects are found`() = runTest {
        // Given
        coEvery { projectRepository.getAllProjects() } returns null

        // When & Then
        val exception = assertThrows<Exception> { getAllProjectsUseCase.getAllProjects() }
        assertThat(exception.message).isEqualTo("No projects found")
    }
}
