package com.berlin.domain.usecase.task_state

import com.berlin.domain.exception.InvalidStateIdException
import com.berlin.domain.repository.TaskStateRepository
import com.berlin.domain.usecase.utils.validation.Validator

class DeleteTaskStateUseCase(
    private val taskStateRepository: TaskStateRepository,
    private val validator: Validator
) {

    operator fun invoke(stateId: String): String {

        if(!validator.isValid(stateId))
            throw InvalidStateIdException("State ID must not be empty or blank")

        return taskStateRepository.deleteState(stateId)
    }
}