package com.berlin.domain.usecase.project

import com.berlin.domain.repository.ProjectRepository
import com.berlin.domain.model.Project

class UpdateProjectUseCase (
    private val projectRepository: ProjectRepository
) {
    suspend fun updateProject(project: Project): Result<String> {
        if(!validateProjectName(project.name))
            throw Exception("Project Name must not be empty or blank")

        return projectRepository.updateProject(project)
            .map { "Updated Successfully" }
            .recover { "Update Failed" }
    }


    private fun validateProjectName(projectName: String): Boolean =
        projectName.isNotBlank() && !(projectName.all { it.isDigit() })
}