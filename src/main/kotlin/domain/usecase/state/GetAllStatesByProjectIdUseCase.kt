package com.berlin.domain.usecase.state


import com.berlin.domain.exception.InvalidProjectIdException
import com.berlin.domain.model.TaskState
import com.berlin.domain.repository.ProjectRepository
import com.berlin.domain.repository.StateRepository

class GetAllStatesByProjectIdUseCase(
    private val stateRepository: StateRepository,
    private val projectRepository: ProjectRepository
) {

    operator fun invoke(projectId: String): List<TaskState> {

        return if (!validateProjectId(projectId)) {
            throw InvalidProjectIdException("Project ID must not be empty or blank")

        } else {
            stateRepository.getStatesByProjectId(projectId)

        }
    }

    private fun validateProjectId(projectId: String): Boolean =
        projectId.isNotBlank() && !(projectId.all { it.isDigit() })

}