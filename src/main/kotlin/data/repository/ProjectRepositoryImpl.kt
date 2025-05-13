package com.berlin.data.repository

import com.berlin.data.BaseDataSource
import com.berlin.data.dto.ProjectDto
import com.berlin.data.mapper.ProjectMapper
import com.berlin.domain.exception.InvalidProjectException
import com.berlin.domain.exception.ProjectNotFoundException
import com.berlin.domain.model.Project
import com.berlin.domain.repository.ProjectRepository

class ProjectRepositoryImpl(
    private val projectDataSource: BaseDataSource<ProjectDto>,
    private val projectMapper: ProjectMapper
) : ProjectRepository {

    override fun createProject(project: Project): String {
        val projectDto = projectMapper.mapToDataModel(project)
        return if (projectDataSource.write(projectDto)) "Creation Successfully"
        else throw InvalidProjectException("can not create project")
    }

    override fun getProjectById(projectId: String): Project {
        return projectMapper.mapToDomainModel(
            projectDataSource.getById(projectId) ?: throw ProjectNotFoundException(projectId)
        )
    }

    override fun getAllProjects(): List<Project> {
        return projectDataSource.getAll().map {
            projectMapper.mapToDomainModel(it)
        }
    }

    override fun updateProject(project: Project): String {
        val projectDto = projectMapper.mapToDataModel(project)
        return if (projectDataSource.update(project.id, projectDto)) "Updated Successfully"
        else throw InvalidProjectException("can not update project")
    }

    override fun deleteProject(projectId: String): String {
        return if (projectDataSource.delete(projectId)) "Deleted Successfully"
        else throw InvalidProjectException("can not delete project")
    }
}