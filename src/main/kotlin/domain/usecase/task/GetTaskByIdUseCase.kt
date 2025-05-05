package com.berlin.domain.usecase.task

import com.berlin.domain.exception.InvalidTaskIdException
import com.berlin.domain.model.Task
import com.berlin.domain.repository.TaskRepository

class GetTaskByIdUseCase(
    private val taskRepository: TaskRepository,
) {

    operator fun invoke(taskId: String): Result<Task> {

        if (!validateTaskId(taskId)) {
            throw InvalidTaskIdException("Task id must not be empty, blank, or purely numeric")
        }

        return taskRepository.findById(taskId)
    }

    private fun validateTaskId(id: String): Boolean =
        id.isNotBlank() && !id.all { it.isDigit() }
}
