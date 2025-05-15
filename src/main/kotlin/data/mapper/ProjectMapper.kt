package com.berlin.data.mapper

import com.berlin.data.dto.ProjectDto
import com.berlin.domain.model.Project

class ProjectMapper : EntityMapper<ProjectDto, Project> {
    override fun mapToDomainModel(from: ProjectDto): Project {
        return Project(
            id = from.id,
            title = from.title,
            statesId = from.statesId,
            description = from.description,
            tasksId = from.tasksId
        )
    }

    override fun mapToDataModel(from: Project): ProjectDto {
        return ProjectDto(
            id = from.id,
            title = from.title,
            statesId = from.statesId,
            description = from.description,
            tasksId = from.tasksId
        )
    }
}