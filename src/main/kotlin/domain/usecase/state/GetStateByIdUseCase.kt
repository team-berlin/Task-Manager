package com.berlin.domain.usecase.state

import com.berlin.domain.exception.InvalidStateIdException
import com.berlin.domain.model.TaskState
import com.berlin.domain.repository.StateRepository

class GetStateByIdUseCase(
    private val stateRepository: StateRepository
) {

    fun getStateById(stateId: String): TaskState {
        if(!validateStateId(stateId))
            throw InvalidStateIdException("State id must not be empty, blank, or purely numeric")

        return stateRepository.getStateById(stateId)
    }

    private fun validateStateId(stateId: String): Boolean =
        stateId.isNotBlank() && !(stateId.all { it.isDigit() })

}