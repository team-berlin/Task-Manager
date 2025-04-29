package com.berlin.logic.usecase.state

import com.berlin.logic.repositories.StateRepository
import com.berlin.model.State

class CreationStateUseCase(
    private val stateRepository: StateRepository
) {
    fun createNewState(state: State) {
        val result = stateRepository.addState(state)
        when {
            result.isSuccess -> Result.success("Created Successfully")
            result.isFailure -> Result.failure(Exception("Creation Failed"))
        }
    }
}
