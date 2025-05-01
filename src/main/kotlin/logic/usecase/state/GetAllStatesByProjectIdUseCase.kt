package com.berlin.logic.usecase.state

import com.berlin.logic.repositories.ProjectRepository
import com.berlin.logic.repositories.StateRepository
import com.berlin.model.State

class GetAllStatesByProjectIdUseCase(
    private val stateRepository: StateRepository,
    private val projectRepository: ProjectRepository
) {

    fun getAllStatesByProjectId(projectId: String): List<State>? {

        if (!validateProjectId(projectId))
            throw Exception("Project ID must not be empty or blank")

        if (checkProjectExists(projectId)) {
            return stateRepository.getStatesByProjectId(projectId)
                ?: throw Exception("No tasks found for state ID $projectId")
        } else {
            throw Exception("Project with ID $projectId does not exist")
        }
    }

    private fun checkProjectExists(projectId: String): Boolean =
        projectRepository.getProjectById(projectId) != null

    private fun validateProjectId(projectId: String): Boolean =
        projectId.isNotBlank() && !(projectId.all { it.isDigit() })

}