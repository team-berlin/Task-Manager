package com.berlin.domain.usecase.state


import com.berlin.domain.exception.InvalidStateNameException
import com.berlin.domain.repository.StateRepository
import com.berlin.domain.model.TaskState
import com.berlin.domain.usecase.utils.id_generator.IdGeneratorImplementation

class CreateStateUseCase(
    private val stateRepository: StateRepository,
    private val idGenerator: IdGeneratorImplementation,
) {
    fun createNewState(stateName: String, projectId: String): String {
        if (validateStateName(stateName)) {
            val newState = TaskState(
                id = idGenerator.generateId(stateName),
                name = stateName,
                projectId = projectId
            )
            return stateRepository.addState(newState)
        } else {
            throw InvalidStateNameException("State Name must not be empty or blank")
        }
    }

    private fun validateStateName(stateName: String): Boolean =
        stateName.isNotBlank() && !(stateName.all { it.isDigit() })
}
