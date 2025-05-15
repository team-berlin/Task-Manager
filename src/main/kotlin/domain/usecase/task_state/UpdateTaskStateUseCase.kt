package com.berlin.domain.usecase.task_state

import com.berlin.domain.exception.InvalidStateNameException
import com.berlin.domain.model.TaskState
import com.berlin.domain.repository.TaskStateRepository
import com.berlin.domain.usecase.utils.validation.Validator

class UpdateTaskStateUseCase(
    private val taskStateRepository: TaskStateRepository,
    private val validator: Validator
) {
    operator fun invoke(stateId: String, newStateName: String, projectId: String): String {
        if (!validator.isValid(newStateName))
            throw InvalidStateNameException("State Name must not be empty or blank")
        val updatedState = TaskState(
            id = stateId,
            name = newStateName,
            projectId = projectId
        )
        return taskStateRepository.updateState(updatedState)
    }

}