package com.berlin.domain.usecase.task_state

import com.berlin.domain.exception.InvalidTaskIdException
import com.berlin.domain.model.TaskState
import com.berlin.domain.repository.TaskStateRepository
import com.berlin.domain.repository.TaskRepository
import com.berlin.domain.usecase.utils.validation.Validator

class GetTaskStateByTaskIdUseCase(
    private val taskStateRepository: TaskStateRepository,
    private val taskRepository: TaskRepository,
    private val validator: Validator
) {

    operator fun invoke(taskId: String): TaskState? {
        if (!validator.isValid(taskId)) {
            throw InvalidTaskIdException("Task ID must not be empty or blank")

        } else {
            return taskStateRepository.getStateByTaskId(taskId)
        }
    }
}