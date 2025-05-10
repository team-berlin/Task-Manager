package data.project

import com.berlin.data.BaseDataSource
import com.berlin.data.csv_data_source.CsvDataSource
import com.berlin.data.project.ProjectRepositoryImpl
import com.berlin.domain.exception.InvalidProjectException
import com.berlin.domain.model.Project
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class ProjectRepositoryImplTest {
    private lateinit var repository: ProjectRepositoryImpl
    private val csvDataSource: BaseDataSource<Project> = mockk()

    @BeforeEach
    fun setUp() {
        repository = ProjectRepositoryImpl(csvDataSource)
    }

    //region createProject

    @Test
    fun `createProject should return success when created succeeds`() = runTest {
        // Given
        coEvery { csvDataSource.write(any()) } returns true
        // When
        val result = repository.createProject(validProject)
        // Then
        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `createProject should return success with project id when created succeeds`() = runTest {
        // Given
        coEvery { csvDataSource.write(any()) } returns true
        // When
        val result = repository.createProject(validProject)
        // Then
        assertThat(result.getOrNull()).isEqualTo(validProject.id)
    }

    @Test
    fun `createProject should return failure when created fails`() = runTest {
        // Given
        coEvery { csvDataSource.write(any()) } returns false
        // When
        val result = repository.createProject(validProject)
        // Then
        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `createProject should return failure with InvalidProjectException when created fails`() = runTest {
        // Given
        coEvery { csvDataSource.write(any()) } returns false
        // When
        val result = repository.createProject(validProject)
        // Then
        assertThat(result.exceptionOrNull()).isInstanceOf(InvalidProjectException::class.java)
    }
    //endregion

    //region getProjectById

    @Test
    fun `getProjectById should return project when data source return project`() = runTest {
        // Given
        coEvery { csvDataSource.getById(any()) } returns validProject
        // When
        val result = repository.getProjectById("projectId")
        // Then
        assertThat(result).isEqualTo(validProject)
    }

    @Test
    fun `getProjectById should return null when data source return null`() = runTest {
        // Given
        coEvery { csvDataSource.getById(any()) } returns null
        // When
        val result = repository.getProjectById("projectId")
        // Then
        assertThat(result).isNull()
    }
    //endregion

    //region getAllProjects

    @Test
    fun `getAllProjects should return null when data source return empty list`() = runTest {
        // Given
        coEvery { csvDataSource.getAll() } returns emptyList()
        // When
        val result = repository.getAllProjects()
        // Then
        assertThat(result).isNull()
    }

    @Test
    fun `getAllProjects should return list of projects when data source return list of projects`() = runTest {
        // Given
        coEvery { csvDataSource.getAll() } returns projects
        // When
        val result = repository.getAllProjects()
        // Then
        assertThat(result).isEqualTo(projects)
    }
    //endregion

    //region updateProject

    @Test
    fun `updateProject should return success when updated succeeds`() = runTest {
        // Given
        coEvery { csvDataSource.update(any(), any()) } returns true
        // When
        val result = repository.updateProject(validProject)
        // Then
        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `updateProject should return success with project id when updated succeeds`() = runTest {
        // Given
        coEvery { csvDataSource.update(any(), any()) } returns true
        // When
        val result = repository.updateProject(validProject)
        // Then
        assertThat(result.getOrNull()).isEqualTo(validProject.id)
    }

    @Test
    fun `updateProject should return failure when updated fails`() = runTest {
        // Given
        coEvery { csvDataSource.update(any(), any()) } returns false
        // When
        val result = repository.updateProject(validProject)
        // Then
        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `updateProject should return failure with InvalidProjectException when updated fails`() = runTest {
        // Given
        coEvery { csvDataSource.update(any(), any()) } returns false
        // When
        val result = repository.updateProject(validProject)
        // Then
        assertThat(result.exceptionOrNull()).isInstanceOf(InvalidProjectException::class.java)
    }
    //endregion

    //region deleteProject
    @Test
    fun `deleteProject should return success when deleted succeeds`() = runTest {
        // Given
        coEvery { csvDataSource.delete(any()) } returns true
        // When
        val result = repository.deleteProject(validProject.id)
        // Then
        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `deleteProject should return success with project id when deleted succeeds`() = runTest {
        // Given
        coEvery { csvDataSource.delete(any()) } returns true
        // When
        val result = repository.deleteProject(validProject.id)
        // Then
        assertThat(result.getOrNull()).isEqualTo(validProject.id)
    }

    @Test
    fun `deleteProject should return failure when deleted fails`() = runTest {
        // Given
        coEvery { csvDataSource.delete(any()) } returns false
        // When
        val result = repository.deleteProject(validProject.id)
        // Then
        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `deleteProject should return failure with InvalidProjectException when deleted fails`() = runTest {
        // Given
        coEvery { csvDataSource.delete(any()) } returns false
        // When
        val result = repository.deleteProject(validProject.id)
        // Then
        assertThat(result.exceptionOrNull()).isInstanceOf(InvalidProjectException::class.java)
    }
    //endregion

    companion object {
        val validProject = Project(id = "tt", name = "aaa", null, null, null)
        val projects = listOf(
            Project(id = "tt", name = "aaa", null, null, null),
            Project(id = "dd", name = "eds", null, null, null),
            Project(id = "dds", name = "fsd", null, null, null)
        )
    }

}