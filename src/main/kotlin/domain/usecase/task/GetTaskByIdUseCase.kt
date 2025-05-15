package com.berlin.domain.usecase.task

import com.berlin.domain.exception.InvalidTaskIdException
import com.berlin.domain.model.Task
import com.berlin.domain.repository.TaskRepository
import com.berlin.domain.usecase.utils.isIDValid

class GetTaskByIdUseCase(
    private val taskRepository: TaskRepository,
) {

    operator fun invoke(taskId: String): Task {

        if (isIDValid(taskId).not()) {
            throw InvalidTaskIdException("Task id must not be empty, blank, or purely numeric")
        }

        return taskRepository.getTaskById(taskId)
    }


}
