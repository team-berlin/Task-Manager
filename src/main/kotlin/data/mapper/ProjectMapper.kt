package com.berlin.data.mapper

import com.berlin.data.dto.ProjectDto
import com.berlin.domain.model.Project

class ProjectMapper : EntityMapper<ProjectDto, Project> {
    override fun mapToDomainModel(projectDto: ProjectDto): Project {
        return Project(
            id = projectDto.id,
            title = projectDto.title,
            statesId = projectDto.statesId,
            description = projectDto.description,
            tasksId = projectDto.tasksId
        )
    }

    override fun mapToDataModel(project: Project): ProjectDto {
        return ProjectDto(
            id = project.id,
            title = project.title,
            statesId = project.statesId,
            description = project.description,
            tasksId = project.tasksId
        )
    }
}