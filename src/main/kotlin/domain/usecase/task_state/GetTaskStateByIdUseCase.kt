package com.berlin.domain.usecase.task_state

import com.berlin.domain.exception.InvalidStateIdException
import com.berlin.domain.model.TaskState
import com.berlin.domain.repository.TaskStateRepository

class GetTaskStateByIdUseCase(
    private val taskStateRepository: TaskStateRepository
) {

    operator fun invoke(stateId: String): TaskState {
        if(!validateStateId(stateId))
            throw InvalidStateIdException("State id must not be empty, blank, or purely numeric")

        return taskStateRepository.getStateById(stateId)
    }

    private fun validateStateId(stateId: String): Boolean =
        stateId.isNotBlank() && !(stateId.all { it.isDigit() })

}