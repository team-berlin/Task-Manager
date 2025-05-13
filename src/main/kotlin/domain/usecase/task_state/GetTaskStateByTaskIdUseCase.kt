package com.berlin.domain.usecase.task_state

import com.berlin.domain.exception.InvalidTaskIdException
import com.berlin.domain.model.TaskState
import com.berlin.domain.repository.TaskStateRepository
import com.berlin.domain.repository.TaskRepository

class GetTaskStateByTaskIdUseCase(
    private val taskStateRepository: TaskStateRepository,
    private val taskRepository: TaskRepository
) {

    operator fun invoke(taskId: String): TaskState? {
        if (!validateTaskId(taskId)) {
            throw InvalidTaskIdException("Task ID must not be empty or blank")

        } else {
            return taskStateRepository.getStateByTaskId(taskId)
        }
    }

    private fun validateTaskId(taskId: String): Boolean = taskId.isNotBlank() && !(taskId.all { it.isDigit() })

}