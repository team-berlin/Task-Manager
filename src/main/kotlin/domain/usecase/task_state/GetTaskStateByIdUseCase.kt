package com.berlin.domain.usecase.task_state

import com.berlin.domain.exception.InvalidStateIdException
import com.berlin.domain.model.TaskState
import com.berlin.domain.repository.TaskStateRepository
import com.berlin.domain.usecase.utils.isIDValid

class GetTaskStateByIdUseCase(
    private val taskStateRepository: TaskStateRepository
) {

    operator fun invoke(stateId: String): TaskState {
        if(isIDValid(stateId).not())
            throw InvalidStateIdException("State id must not be empty, blank, or purely numeric")

        return taskStateRepository.getStateById(stateId)
    }



}