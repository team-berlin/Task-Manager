package com.berlin.logic.usecase.project

import com.berlin.logic.repositories.ProjectRepository
import com.berlin.model.Project

class GetAllProjectsUseCase(
    private val projectRepository: ProjectRepository
) {
    fun getAllProjects(): List<Project> {
        return projectRepository.getAllProjects()
            ?: throw Exception("No projects found")
    }
}