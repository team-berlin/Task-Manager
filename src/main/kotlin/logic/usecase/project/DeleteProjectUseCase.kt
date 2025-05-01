package com.berlin.logic.usecase.project

import com.berlin.logic.repositories.ProjectRepository

class DeleteProjectUseCase (
    private val projectRepository: ProjectRepository
) {
    fun deleteProject(projectId: String): Result<String> {

        if(!validateProjectId(projectId))
            throw Exception("Project ID must not be empty or blank")

        if (!checkProjectExists(projectId)) {
            return Result.failure(
                Exception("Project with ID $projectId does not exist")
            )
        }

        return projectRepository.deleteProject(projectId)
            .map { "Deleted Successfully" }
            .recover { "Deletion Failed" }
    }

    private fun validateProjectId(projectId: String): Boolean =
        projectId.isNotBlank() && !(projectId.all { it.isDigit() })

    private fun checkProjectExists(projectId: String): Boolean =
        projectRepository.getProjectById(projectId) != null
}