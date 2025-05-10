package com.berlin.domain.usecase.project

import com.berlin.domain.repository.ProjectRepository
import com.berlin.domain.model.Project

class GetAllProjectsUseCase(
    private val projectRepository: ProjectRepository
) {
    suspend fun getAllProjects(): List<Project> {
        return projectRepository.getAllProjects()
            ?: throw Exception("No projects found")
    }
}