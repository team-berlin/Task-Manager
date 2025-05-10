package com.berlin.domain.usecase.project

import com.berlin.domain.repository.ProjectRepository
import com.berlin.domain.model.Project

class GetProjectByIdUseCase (
    private val projectRepository: ProjectRepository
) {

    suspend fun getProjectById(projectId: String): Project {
        if(!validateProjectId(projectId))
            throw Exception("Project ID must not be empty or blank")

        return projectRepository.getProjectById(projectId)
            ?: throw Exception("Project with ID $projectId does not exist")
    }

    private fun validateProjectId(projectId: String): Boolean =
        projectId.isNotBlank() && !(projectId.all { it.isDigit() })

}