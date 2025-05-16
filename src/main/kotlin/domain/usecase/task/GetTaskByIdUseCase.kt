package com.berlin.domain.usecase.task

import com.berlin.domain.exception.InvalidTaskIdException
import com.berlin.domain.model.Task
import com.berlin.domain.repository.TaskRepository
import com.berlin.domain.usecase.utils.validation.Validator

class GetTaskByIdUseCase(
    private val taskRepository: TaskRepository,
    private val validator: Validator
) {

    operator fun invoke(taskId: String): Task {

        if (!validator.isValid(taskId)) {
            throw InvalidTaskIdException("Task id must not be empty, blank, or purely numeric")
        }

        return taskRepository.getTaskById(taskId)
    }
}
