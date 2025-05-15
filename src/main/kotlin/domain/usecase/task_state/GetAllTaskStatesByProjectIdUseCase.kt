package com.berlin.domain.usecase.task_state


import com.berlin.domain.exception.InvalidProjectIdException
import com.berlin.domain.model.TaskState
import com.berlin.domain.repository.ProjectRepository
import com.berlin.domain.repository.TaskStateRepository

class GetAllTaskStatesByProjectIdUseCase(
    private val taskStateRepository: TaskStateRepository,
    private val projectRepository: ProjectRepository
) {

    operator fun invoke(projectId: String): List<TaskState> {

        return if (!validateProjectId(projectId)) {
            throw InvalidProjectIdException("Project ID must not be empty or blank")

        } else {
            taskStateRepository.getStatesByProjectId(projectId)

        }
    }

    private fun validateProjectId(projectId: String): Boolean =
        projectId.isNotBlank() && !(projectId.all { it.isDigit() })

}