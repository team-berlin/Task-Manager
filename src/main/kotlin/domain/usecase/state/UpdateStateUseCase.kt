package com.berlin.domain.usecase.state

import com.berlin.domain.exception.InvalidStateNameException
import com.berlin.domain.model.State
import com.berlin.domain.repository.StateRepository

class UpdateStateUseCase(
    private val stateRepository: StateRepository
) {
    fun updateState(state: State): Result<String> {
        if(!validateStateName(state.name))
            throw InvalidStateNameException("State Name must not be empty or blank")

        return stateRepository.updateState(state)
            .map { "Updated Successfully" }
            .recover { "Update Failed" }
    }


    private fun validateStateName(stateName: String): Boolean =
        stateName.isNotBlank() && !(stateName.all { it.isDigit() })
}