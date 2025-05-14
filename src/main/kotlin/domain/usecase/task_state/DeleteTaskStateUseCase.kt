package com.berlin.domain.usecase.task_state

import com.berlin.domain.exception.InvalidStateIdException
import com.berlin.domain.repository.TaskStateRepository

class DeleteTaskStateUseCase(
    private val taskStateRepository: TaskStateRepository
) {

    operator fun invoke(stateId: String): String {

        if(!validateStateId(stateId))
            throw InvalidStateIdException("State ID must not be empty or blank")

        return taskStateRepository.deleteState(stateId)
    }

    private fun validateStateId(stateId: String): Boolean =
        stateId.isNotBlank() && !(stateId.all { it.isDigit() })
}