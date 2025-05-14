package com.berlin.domain.usecase.task_state

import com.berlin.domain.exception.InvalidStateIdException
import com.berlin.domain.exception.TaskNotFoundException
import com.berlin.domain.model.Task
import com.berlin.domain.repository.TaskStateRepository

class GetTasksByTaskStateIdUseCase(
    private val taskStateRepository: TaskStateRepository,
) {

    operator fun invoke(stateId: String): List<Task> {
        if (!validateStateId(stateId)) {
            throw InvalidStateIdException("State ID must not be empty or blank")
        } else {
            return taskStateRepository.getTasksByStateId(stateId)
                ?: throw TaskNotFoundException("No tasks found for state ID $stateId")
        }
    }

    private fun validateStateId(stateId: String): Boolean = stateId.isNotBlank() && !(stateId.all { it.isDigit() })
}