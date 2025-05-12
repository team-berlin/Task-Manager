package com.berlin.domain.usecase.project

import com.berlin.domain.exception.InvalidProjectIdException
import com.berlin.domain.repository.ProjectRepository
import com.berlin.domain.model.Project

class GetProjectByIdUseCase (
    private val projectRepository: ProjectRepository
) {

    fun getProjectById(projectId: String): Project {
        if(!validateProjectId(projectId))
            throw InvalidProjectIdException("project id must not be empty, blank, or purely numeric")

        return projectRepository.getProjectById(projectId)
    }

    private fun validateProjectId(projectId: String): Boolean =
        projectId.isNotBlank() && !(projectId.all { it.isDigit() })

}