package com.berlin.data.project

import com.berlin.data.csv_data_source.CsvDataSource
import com.berlin.domain.exception.InvalidProjectException
import com.berlin.domain.model.Project
import com.berlin.domain.repository.ProjectRepository

class ProjectRepositoryImpl(private val projectDataSource: CsvDataSource<Project>):ProjectRepository {
    override fun createProject(project: Project): Result<String> {
        return Result.failure(InvalidProjectException(""))
    }

    override fun getProjectById(projectId: String): Project? {
        return null
    }

    override fun getAllProjects(): List<Project>? {
        return null
    }

    override fun updateProject(project: Project): Result<String> {
        return Result.failure(InvalidProjectException(""))
    }

    override fun deleteProject(projectId: String): Result<String> {
        return Result.failure(InvalidProjectException(""))
    }
}