package com.berlin.domain.usecase.task

import com.berlin.domain.exception.InvalidProjectIdException
import com.berlin.domain.model.Task
import com.berlin.domain.repository.TaskRepository
import com.berlin.domain.usecase.utils.isIDValid

class GetTasksByProjectUseCase(
    private val taskRepository: TaskRepository
) {

    operator fun invoke(projectId: String): List<Task> {

        if (isIDValid(projectId).not()) {
            throw InvalidProjectIdException("Project id must not be empty, blank, or purely numeric")
        }
        return taskRepository.getTasksByProjectId(projectId)
    }


}