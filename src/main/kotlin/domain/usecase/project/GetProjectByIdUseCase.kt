package com.berlin.domain.usecase.project

import com.berlin.domain.exception.InvalidProjectIdException
import com.berlin.domain.repository.ProjectRepository
import com.berlin.domain.model.Project
import com.berlin.domain.usecase.utils.validation.Validator

class GetProjectByIdUseCase (
    private val projectRepository: ProjectRepository,
    private val validator: Validator
) {

    operator fun invoke(projectId: String): Project {
        if(!validator.isValid(projectId))
            throw InvalidProjectIdException("project id must not be empty, blank, or purely numeric")

        return projectRepository.getProjectById(projectId)
    }
}