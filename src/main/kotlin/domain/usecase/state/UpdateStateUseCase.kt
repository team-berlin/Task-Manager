package com.berlin.domain.usecase.state

import com.berlin.domain.exception.InvalidStateNameException
import com.berlin.domain.model.TaskState
import com.berlin.domain.repository.StateRepository

class UpdateStateUseCase(
    private val stateRepository: StateRepository,
) {
    fun updateState(stateId: String, newStateName: String, projectId: String): Result<String> {
        if (!validateStateName(newStateName))
            throw InvalidStateNameException("State Name must not be empty or blank")
        val updatedState = TaskState(
            id = stateId,
            name = newStateName,
            projectId = projectId
        )
        return stateRepository.updateState(updatedState)
            .map { "Updated Successfully" }
            .recover { "Update Failed" }
    }


    private fun validateStateName(stateName: String): Boolean =
        stateName.isNotBlank() && !(stateName.all { it.isDigit() })
}