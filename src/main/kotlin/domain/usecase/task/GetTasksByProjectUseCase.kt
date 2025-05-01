package com.berlin.domain.usecase.task

import com.berlin.domain.exception.InvalidProjectIdException
import com.berlin.domain.model.Task
import com.berlin.domain.repository.TaskRepository

class GetTasksByProjectUseCase(
    private val taskRepository: TaskRepository
) {

    operator fun invoke(projectId: String): Result<List<Task>> {

        if (!validateProjectId(projectId)) {
            throw InvalidProjectIdException("Project id must not be empty, blank, or purely numeric")
        }
        return taskRepository.findTasksByProjectId(projectId)
    }

    private fun validateProjectId(id: String): Boolean =
        id.isNotBlank() && !id.all { it.isDigit() }
}
