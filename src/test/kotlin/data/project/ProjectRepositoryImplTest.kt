package com.berlin.data.project

import com.berlin.data.BaseDataSource
import com.berlin.data.dto.ProjectDto
import com.berlin.data.mapper.ProjectMapper
import com.berlin.data.repository.ProjectRepositoryImpl
import com.berlin.domain.exception.InvalidProjectException
import com.berlin.domain.model.Project
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
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
    fun `createProject should return success when created succeeds`() {
        // Given
        every { csvDataSource.write(any()) } returns true
        // When
        val result = repository.createProject(validProject)
        // Then
            assertThat(result).isEqualTo("Creation Successfully")
    }

    @Test
    fun `createProject should return success with project id when created succeeds`() {
        // Given
        every { csvDataSource.write(any()) } returns true
        // When
        val result = repository.createProject(validProject)
        // Then
        assertThat(result).isEqualTo(validProject)
    }


    @Test
    fun `createProject should return failure with InvalidProjectException when created fails`() {
        // Given
        every { csvDataSource.write(any()) } returns false
        // When
        val result = repository.createProject(validProject)
        // Then
        assertThat(result).isInstanceOf(InvalidProjectException::class.java)
    }
    //endregion

    //region getProjectById

    @Test
    fun `getProjectById should return project when data source return project`() {
        // Given
        every { csvDataSource.getById(any()) } returns validProjectDto
        // When
        val result = repository.getProjectById("projectId")
        // Then
        assertThat(result).isEqualTo(validProject)
    }

    @Test
    fun `getProjectById should return null when data source return null`() {
        // Given
        every { csvDataSource.getById(any()) } returns null
        // When
        val result = repository.getProjectById("projectId")
        // Then
        assertThat(result).isNull()
    }
    //endregion

    //region getAllProjects

    @Test
    fun `getAllProjects should return null when data source return empty list`() {
        // Given
        every { csvDataSource.getAll() } returns emptyList()
        // When
        val result = repository.getAllProjects()
        // Then
        assertThat(result).isNull()
    }

    @Test
    fun `getAllProjects should return list of projects when data source return list of projects`() {
        // Given
        every { csvDataSource.getAll() } returns listOf(validProjectDto)
        // When
        val result = repository.getAllProjects()
        // Then
        assertThat(result).isEqualTo(projects)
    }
    //endregion

    //region updateProject

    @Test
    fun `updateProject should return success when updated succeeds`() {
        // Given
        every { csvDataSource.update(any(), any()) } returns true
        // When
        val result = repository.updateProject(validProject)
        // Then
        assertThat(result).isEqualTo("Updated Successfully")
    }

    @Test
    fun `updateProject should return success with project id when updated succeeds`() {
        // Given
        every { csvDataSource.update(any(), any()) } returns true
        // When
        val result = repository.updateProject(validProject)
        // Then
        assertThat(result).isEqualTo(validProject.id)
    }

    @Test
    fun `updateProject should return failure when updated fails`() {
        // Given
        every { csvDataSource.update(any(), any()) } returns false
        // When
        val result = repository.updateProject(validProject)
        // Then
        assertThat(result).isEqualTo("can not update project")
    }

    @Test
    fun `updateProject should return failure with InvalidProjectException when updated fails`() {
        // Given
        every { csvDataSource.update(any(), any()) } returns false
        // When
        val result = repository.updateProject(validProject)
        // Then
        assertThat(result).isInstanceOf(InvalidProjectException::class.java)
    }
    //endregion

    //region deleteProject
    @Test
    fun `deleteProject should return success when deleted succeeds`() {
        // Given
        every { csvDataSource.delete(any()) } returns true
        // When
        val result = repository.deleteProject(validProject.id)
        // Then
        assertThat(result).isEqualTo("Deleted Successfully")
    }

    @Test
    fun `deleteProject should return success with project id when deleted succeeds`() {
        // Given
        every { csvDataSource.delete(any()) } returns true
        // When
        val result = repository.deleteProject(validProject.id)
        // Then
        assertThat(result).isEqualTo(validProject.id)
    }

    @Test
    fun `deleteProject should return failure when deleted fails`() {
        // Given
        every { csvDataSource.delete(any()) } returns false
        // When
        val result = repository.deleteProject(validProject.id)
        // Then
        assertThat(result).isEqualTo("can not delete project")
    }

    @Test
    fun `deleteProject should return failure with InvalidProjectException when deleted fails`() {
        // Given
        every { csvDataSource.delete(any()) } returns false
        // When
        val result = repository.deleteProject(validProject.id)
        // Then
        assertThat(result).isInstanceOf(InvalidProjectException::class.java)
    }
    //endregion

    companion object {
        val validProject = Project(id = "tt", title = "aaa", null,
            null, null)

        val validProjectDto = ProjectDto(id = "tt", title = "aaa", null,
            null, null)

        val projects = listOf(
            Project(id = "tt", title = "aaa", null, null,
                null),
            Project(id = "dd", title = "eds", null, null,
                null),
            Project(id = "dds", title = "fsd", null, null,
                null)
        )
    }

}