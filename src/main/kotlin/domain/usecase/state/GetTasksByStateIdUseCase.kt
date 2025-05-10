package com.berlin.domain.usecase.state

import com.berlin.domain.exception.InvalidStateIdException
import com.berlin.domain.exception.InvalidTaskStateException
import com.berlin.domain.exception.TaskNotFoundException
import com.berlin.domain.model.Task
import com.berlin.domain.repository.StateRepository

class GetTasksByStateIdUseCase (
    private val stateRepository: StateRepository
) {

    suspend fun getAllTasksByStateId(stateId: String): List<Task>? {
        if (!validateStateId(stateId)) throw InvalidStateIdException("State ID must not be empty or blank")

        if (checkStateExists(stateId)) {
            return stateRepository.getTasksByStateId(stateId)
                ?: throw TaskNotFoundException("No tasks found for state ID $stateId")
        } else {
            throw InvalidStateIdException("State with ID $stateId does not exist")
        }
    }


    private suspend fun checkStateExists(stateId: String): Boolean = stateRepository.getStateById(stateId) != null

    private fun validateStateId(stateId: String): Boolean = stateId.isNotBlank() && !(stateId.all { it.isDigit() })
}