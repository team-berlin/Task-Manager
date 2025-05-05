package com.berlin.domain.usecase.state


import com.berlin.domain.model.State
import com.berlin.domain.repository.ProjectRepository
import com.berlin.domain.repository.StateRepository

class GetAllStatesByProjectIdUseCase(
    private val stateRepository: StateRepository,
    private val projectRepository: ProjectRepository
) {

    fun getAllStatesByProjectId(projectId: String): List<State> {

        if (!validateProjectId(projectId))
            throw Exception("Project ID must not be empty or blank")

        if (checkProjectExists(projectId)) {
            return stateRepository.getStatesByProjectId(projectId)
                ?: throw Exception("No states found for project ID $projectId")
        } else {
            throw Exception("Project with ID $projectId does not exist")
        }
    }

    private fun checkProjectExists(projectId: String): Boolean =
        projectRepository.getProjectById(projectId) != null

    private fun validateProjectId(projectId: String): Boolean =
        projectId.isNotBlank() && !(projectId.all { it.isDigit() })

}