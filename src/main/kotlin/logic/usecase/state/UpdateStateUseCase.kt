package com.berlin.logic.usecase.state

import com.berlin.logic.repositories.StateRepository
import com.berlin.model.State

class UpdateStateUseCase(
    private val stateRepository: StateRepository
) {
    fun updateState(state: State) {
        if (checkStateExisted(state.id)) {
            val result = stateRepository.updateState(state)
            when {
                result.isSuccess -> Result.success("Updated Successfully")
                result.isFailure -> Result.failure(Exception("Update Failed"))
            }
        } else {
            throw Exception("State does not exist")
        }
    }

    private fun checkStateExisted(stateId: String): Boolean {
        return stateRepository.getStateById(stateId)
    }
}