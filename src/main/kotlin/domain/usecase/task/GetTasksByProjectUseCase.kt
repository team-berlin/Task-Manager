package com.berlin.domain.usecase.task

import com.berlin.domain.exception.InvalidProjectIdException
import com.berlin.domain.model.Task
import com.berlin.domain.repository.TaskRepository
import com.berlin.domain.usecase.utils.validation.Validator

class GetTasksByProjectUseCase(
    private val taskRepository: TaskRepository,
    private val validator: Validator
) {

    operator fun invoke(projectId: String): List<Task> {

        if (!validator.isValid(projectId)) {
            throw InvalidProjectIdException("Project id must not be empty, blank, or purely numeric")
        }
        return taskRepository.getTasksByProjectId(projectId)
    }
}