package com.berlin.logic.usecase.state

import com.berlin.logic.repositories.StateRepository
import com.berlin.model.State

class GetStateByIdUseCase(
    private val stateRepository: StateRepository
) {

    fun getStateById(stateId: String): State {
        if(!validateStateId(stateId))
            throw Exception("State ID must not be empty or blank")

        return stateRepository.getStateById(stateId)
            ?: throw Exception("State with ID $stateId does not exist")
    }

    private fun validateStateId(stateId: String): Boolean =
        stateId.isNotBlank() && !(stateId.all { it.isDigit() })

}