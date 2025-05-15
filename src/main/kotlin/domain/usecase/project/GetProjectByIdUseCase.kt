package com.berlin.domain.usecase.project

import com.berlin.domain.exception.InvalidProjectIdException
import com.berlin.domain.repository.ProjectRepository
import com.berlin.domain.model.Project
import com.berlin.domain.usecase.utils.isIDValid

class GetProjectByIdUseCase (
    private val projectRepository: ProjectRepository
) {

    operator fun invoke(projectId: String): Project {
        if(isIDValid(projectId).not())
            throw InvalidProjectIdException("project id must not be empty, blank, or purely numeric")

        return projectRepository.getProjectById(projectId)
    }



}