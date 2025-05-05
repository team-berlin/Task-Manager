package com.berlin.domain.usecase.state

import com.berlin.domain.repository.StateRepository

class DeletionStateUseCase(
    private val stateRepository: StateRepository
) {

    fun deleteState(stateId: String): Result<String> {

        if(!validateStateId(stateId))
            throw Exception("State ID must not be empty or blank")

        if (!checkStateExists(stateId)) {
            return Result.failure(
                Exception("State with ID $stateId does not exist")
            )
        }

        return stateRepository.deleteState(stateId)
            .map { "Deleted Successfully" }
            .recover { "Deletion Failed" }
    }

    private fun validateStateId(stateId: String): Boolean =
        stateId.isNotBlank() || !(stateId.all { it.isDigit() })

    fun checkStateExists(stateId: String): Boolean =
        stateRepository.getStateById(stateId) != null
}
