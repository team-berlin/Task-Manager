package com.berlin.domain.usecase.project

import com.berlin.domain.repository.ProjectRepository
import com.berlin.domain.model.Project

class GetAllProjectsUseCase(
    private val projectRepository: ProjectRepository
) {
    operator fun invoke(): List<Project> {
        return projectRepository.getAllProjects()
    }
}