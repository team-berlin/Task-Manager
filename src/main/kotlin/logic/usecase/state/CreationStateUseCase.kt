package com.berlin.logic.usecase.state

import com.berlin.logic.generateIdHelper.DefaultIdGenerator
import com.berlin.logic.repositories.AuditRepository
import com.berlin.logic.repositories.StateRepository
import com.berlin.model.AuditLog
import com.berlin.model.State

class CreationStateUseCase(
    private val stateRepository: StateRepository,
    private val defaultIdGenerator: DefaultIdGenerator,
) {
    fun createNewState(stateName: String, projectId: String): Result<String> {
        if (validateStateName(stateName)) {
            val newState = State(
                id = defaultIdGenerator.generateId(stateName),
                name = stateName,
                projectId = projectId
            )
            return stateRepository.addState(newState)
                .map { "Creation Successfully" }
                .recover { "Creation Failed" }
        } else {
            throw Exception("State Name must not be empty or blank")
        }
    }

    private fun validateStateName(stateName: String): Boolean =
        stateName.isNotBlank() && !(stateName.all { it.isDigit() })

}
