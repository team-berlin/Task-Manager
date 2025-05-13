package com.berlin.data.project

import com.berlin.data.BaseDataSource
import com.berlin.data.dto.ProjectDto
import com.berlin.data.mapper.ProjectMapper
import com.berlin.data.repository.ProjectRepositoryImpl
import com.berlin.domain.exception.InvalidProjectException
import com.berlin.domain.exception.ProjectNotFoundException
import com.berlin.domain.model.Project
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

class ProjectRepositoryImplTest {
    private lateinit var repository: ProjectRepositoryImpl
    private val csvDataSource: BaseDataSource<ProjectDto> = mockk()
    private val projectMapper: ProjectMapper = mockk()

    @BeforeEach
    fun setUp() {
        repository = ProjectRepositoryImpl(csvDataSource, projectMapper)
    }

    //region createProject

    @Test
    fun `createProject should return Creation Successfully when created succeeds`() {
        // Given
        every { csvDataSource.write(any()) } returns true
        every { projectMapper.mapToDataModel(validProject) } returns validProjectDto
        // When
        val result = repository.createProject(validProject)
        // Then
        assertThat(result).isEqualTo("Creation Successfully")
    }

    @Test
    fun `createProject should Throws InvalidProjectException when created fails`() {
        // Given
        every { csvDataSource.write(any()) } returns false
        every { projectMapper.mapToDataModel(validProject) } returns validProjectDto
        // When // Then
        assertThrows<InvalidProjectException> {
            repository.createProject(validProject)
        }

    }
    //endregion

    //region getProjectById

    @Test
    fun `getProjectById should return project when data source return project`() {
        // Given
        every { csvDataSource.getById(any()) } returns validProjectDto
        every { projectMapper.mapToDomainModel(validProjectDto) } returns validProject
        // When
        val result = repository.getProjectById("projectId")
        // Then
        assertThat(result).isEqualTo(validProject)
    }

    @Test
    fun `getProjectById should Throw ProjectNotFoundException when data source return null`() {
        // Given
        every { csvDataSource.getById(any()) } returns null
        every { projectMapper.mapToDomainModel(validProjectDto) } returns validProject
        // When// Then
        assertThrows<ProjectNotFoundException> {
            repository.getProjectById("projectId")
        }
    }
    //endregion

    //region getAllProjects

    @Test
    fun `getAllProjects should return empty list when data source return empty list`() {
        // Given
        every { csvDataSource.getAll() } returns emptyList()
        // When
        val result = repository.getAllProjects()
        // Then
        assertThat(result).isEqualTo(emptyList<Project>())
    }

    @Test
    fun `getAllProjects should return list of projects when data source return list of projects`() {
        // Given
        every { csvDataSource.getAll() } returns listOf(validProjectDto)
        every { projectMapper.mapToDomainModel(any()) } returns validProject
        // When
        val result = repository.getAllProjects()
        // Then
        assertThat(result).isEqualTo(listOf(validProject))
    }
    //endregion

    //region updateProject

    @Test
    fun `updateProject should return Updated Successfully when updated succeeds`() {
        // Given
        every { csvDataSource.update(any(), any()) } returns true
        every { projectMapper.mapToDataModel(validProject) } returns validProjectDto
        // When
        val result = repository.updateProject(validProject)
        // Then
        assertThat(result).isEqualTo("Updated Successfully")
    }

    @Test
    fun `updateProject should return throw InvalidProjectException when updated fails`() {
        // Given
        every { csvDataSource.update(any(), any()) } returns false
        every { projectMapper.mapToDataModel(validProject) } returns validProjectDto
        // When// Then
        assertThrows<InvalidProjectException> {
            repository.updateProject(validProject)
        }
    }

    //endregion

    //region deleteProject
    @Test
    fun `deleteProject should return Deleted Successfully when deleted succeeds`() {
        // Given
        every { csvDataSource.delete(any()) } returns true
        // When
        val result = repository.deleteProject(validProject.id)
        // Then
        assertThat(result).isEqualTo("Deleted Successfully")
    }

    @Test
    fun `deleteProject should throw InvalidProjectException when deleted fails`() {
        // Given
        every { csvDataSource.delete(any()) } returns false
        // When// Then
        assertThrows<InvalidProjectException> {
            repository.deleteProject(validProject.id)
        }
    }
    //endregion

    companion object {
        val validProject = Project(
            id = "tt", title = "aaa", null, null, null
        )
        val validProjectDto = ProjectDto(
            id = "tt", title = "aaa", null, null, null
        )
    }

}