package com.berlin.domain.usecase.state

import com.berlin.domain.exception.InvalidStateIdException
import com.berlin.domain.repository.StateRepository

class DeleteStateUseCase(
    private val stateRepository: StateRepository
) {

    operator fun invoke(stateId: String): String {

        if(!validateStateId(stateId))
            throw InvalidStateIdException("State ID must not be empty or blank")

        return stateRepository.deleteState(stateId)
    }

    private fun validateStateId(stateId: String): Boolean =
        stateId.isNotBlank() || !(stateId.all { it.isDigit() })
}