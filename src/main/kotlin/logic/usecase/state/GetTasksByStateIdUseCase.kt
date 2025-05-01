package com.berlin.logic.usecase.state

import com.berlin.logic.repositories.StateRepository
import com.berlin.model.Task

class GetTasksByStateIdUseCase (
    private val stateRepository: StateRepository
) {

    fun getAllTasksByStateId(stateId: String): List<Task>? {
        if (!validateStateId(stateId)) throw Exception("State ID must not be empty or blank")

        if (checkStateExists(stateId)) {
            return stateRepository.getTasksByStateId(stateId)
                ?: throw Exception("No tasks found for state ID $stateId")
        } else {
            throw Exception("State with ID $stateId does not exist")
        }
    }


    private fun checkStateExists(stateId: String): Boolean = stateRepository.getStateById(stateId) != null

    private fun validateStateId(stateId: String): Boolean = stateId.isNotBlank() && !(stateId.all { it.isDigit() })
}