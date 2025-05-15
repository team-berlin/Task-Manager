package com.berlin.domain.usecase.task_state

import com.berlin.domain.exception.InvalidStateIdException
import com.berlin.domain.model.TaskState
import com.berlin.domain.repository.TaskStateRepository
import com.berlin.domain.usecase.utils.validation.Validator

class GetTaskStateByIdUseCase(
    private val taskStateRepository: TaskStateRepository,
    private val validator: Validator
) {

    operator fun invoke(stateId: String): TaskState {
        if(!validator.isValid(stateId))
            throw InvalidStateIdException("State id must not be empty, blank, or purely numeric")

        return taskStateRepository.getStateById(stateId)
    }
}