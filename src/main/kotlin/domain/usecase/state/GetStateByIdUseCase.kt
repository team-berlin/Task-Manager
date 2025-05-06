package com.berlin.domain.usecase.state

import com.berlin.domain.exception.InvalidStateIdException
import com.berlin.domain.model.State
import com.berlin.domain.repository.StateRepository

class GetStateByIdUseCase(
    private val stateRepository: StateRepository
) {

    fun getStateById(stateId: String): State {
        if(!validateStateId(stateId))
            throw InvalidStateIdException("State ID must not be empty or blank")

        return stateRepository.getStateById(stateId)
            ?: throw InvalidStateIdException("State with ID $stateId does not exist")
    }

    private fun validateStateId(stateId: String): Boolean =
        stateId.isNotBlank() && !(stateId.all { it.isDigit() })

}