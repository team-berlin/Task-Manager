package com.berlin.logic.usecase.project

import com.berlin.logic.repositories.ProjectRepository
import com.berlin.model.Project

class GetProjectByIdUseCase (
    private val projectRepository: ProjectRepository
) {

    fun getProjectById(projectId: String): Project {
        if(!validateProjectId(projectId))
            throw Exception("Project ID must not be empty or blank")

        return projectRepository.getProjectById(projectId)
            ?: throw Exception("Project with ID $projectId does not exist")
    }

    private fun validateProjectId(projectId: String): Boolean =
        projectId.isNotBlank() && !(projectId.all { it.isDigit() })

}