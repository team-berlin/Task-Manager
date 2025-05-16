package com.berlin.domain.usecase.task_state

import com.berlin.domain.exception.InvalidStateIdException
import com.berlin.domain.exception.TaskNotFoundException
import com.berlin.domain.model.Task
import com.berlin.domain.repository.TaskStateRepository
import com.berlin.domain.usecase.utils.validation.Validator

class GetTasksByTaskStateIdUseCase(
    private val taskStateRepository: TaskStateRepository,
    private val validator: Validator
) {

    operator fun invoke(stateId: String): List<Task> {
        if (!validator.isValid(stateId)) {
            throw InvalidStateIdException("State ID must not be empty or blank")
        } else {
            return taskStateRepository.getTasksByStateId(stateId)
                ?: throw TaskNotFoundException("No tasks found for state ID $stateId")
        }
    }
}