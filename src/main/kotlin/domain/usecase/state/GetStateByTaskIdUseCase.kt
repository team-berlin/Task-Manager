package com.berlin.domain.usecase.state

import com.berlin.domain.exception.InvalidTaskIdException
import com.berlin.domain.exception.InvalidTaskStateException
import com.berlin.domain.model.TaskState
import com.berlin.domain.repository.StateRepository
import com.berlin.domain.repository.TaskRepository

class GetStateByTaskIdUseCase(
    private val stateRepository: StateRepository,
    private val taskRepository: TaskRepository
) {

    operator fun invoke(taskId: String): TaskState? {
        if (!validateTaskId(taskId)) {
            return stateRepository.getStateByTaskId(taskId)
        } else {
            throw InvalidTaskIdException("Task ID must not be empty or blank")
        }
    }

    private fun validateTaskId(taskId: String): Boolean = taskId.isNotBlank() && !(taskId.all { it.isDigit() })

}