package com.berlin.domain.usecase.task_state


import com.berlin.domain.exception.InvalidProjectIdException
import com.berlin.domain.model.TaskState
import com.berlin.domain.repository.ProjectRepository
import com.berlin.domain.repository.TaskStateRepository
import com.berlin.domain.usecase.utils.isIDValid

class GetAllTaskStatesByProjectIdUseCase(
    private val taskStateRepository: TaskStateRepository,
    private val projectRepository: ProjectRepository
) {

    operator fun invoke(projectId: String): List<TaskState> {

        return if (isIDValid(projectId).not()) {
            throw InvalidProjectIdException("Project ID must not be empty or blank")

        } else {
            taskStateRepository.getStatesByProjectId(projectId)

        }
    }



}