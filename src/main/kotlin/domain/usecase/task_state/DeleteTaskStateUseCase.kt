package com.berlin.domain.usecase.task_state

import com.berlin.domain.exception.InvalidStateIdException
import com.berlin.domain.repository.TaskStateRepository
import com.berlin.domain.usecase.utils.isIDValid

class DeleteTaskStateUseCase(
    private val taskStateRepository: TaskStateRepository
) {

    operator fun invoke(stateId: String): String {

        if(isIDValid(stateId).not())
            throw InvalidStateIdException("State ID must not be empty or blank")

        return taskStateRepository.deleteState(stateId)
    }


}