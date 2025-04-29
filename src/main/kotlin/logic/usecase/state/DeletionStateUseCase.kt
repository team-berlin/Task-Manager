package com.berlin.logic.usecase.state

import com.berlin.logic.repositories.StateRepository

class DeletionStateUseCase(
    private val stateRepository: StateRepository
) {

    fun deleteState(stateId: String) {
        val result = stateRepository.deleteState(stateId)
        when {
            result.isSuccess -> Result.success("Deleted Success")
            result.isFailure -> Result.success("Deleted Failed")
        }
    }
}