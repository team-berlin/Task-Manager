package com.berlin.data.project

import com.berlin.data.BaseDataSource
import com.berlin.domain.exception.InvalidProjectException
import com.berlin.domain.model.Project
import com.berlin.domain.repository.ProjectRepository

class ProjectRepositoryImpl(private val projectDataSource: BaseDataSource<Project>)
    : ProjectRepository {
    override suspend fun createProject(project: Project): Result<String> {
        return if (projectDataSource.write(project))
            Result.success(project.id)
        else
            Result.failure(InvalidProjectException("can not create project"))
    }

    override suspend fun getProjectById(projectId: String): Project? {
        return projectDataSource.getById(projectId)
    }

    override suspend fun getAllProjects(): List<Project>? {
        return projectDataSource.getAll().ifEmpty { null }
    }

    override suspend fun updateProject(project: Project): Result<String> {
        return if (projectDataSource.update(project.id, project))
            Result.success(project.id)
        else
            Result.failure(InvalidProjectException("can not update project"))
    }

    override suspend fun deleteProject(projectId: String): Result<String> {
        return if (projectDataSource.delete(projectId))
            Result.success(projectId)
        else
            Result.failure(InvalidProjectException("can not delete project"))
    }
}