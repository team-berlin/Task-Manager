package com.berlin.logic.usecase.state

import com.berlin.logic.generateIdHelper.IdGenerator
import com.berlin.logic.repositories.StateRepository
import com.berlin.domain.model.State

class CreationStateUseCase(
    private val stateRepository: StateRepository,
    private val idGenerator: IdGenerator,
) {
    fun createNewState(stateName: String, projectId: String): Result<String> {
        if (validateStateName(stateName)) {
            val newState = State(
                id = idGenerator.generateId(stateName),
                name = stateName,
                projectId = projectId
            )
            return stateRepository.addState(newState)
                .map { "State created successfully" }
                .recover { "Creation Failed" }
        } else {
            throw Exception("State Name must not be empty or blank")
        }
    }

    private fun validateStateName(stateName: String): Boolean =
        stateName.isNotBlank() && !(stateName.all { it.isDigit() })

}
