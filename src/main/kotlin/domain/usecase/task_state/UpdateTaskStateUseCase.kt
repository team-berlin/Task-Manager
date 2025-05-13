package com.berlin.domain.usecase.task_state

import com.berlin.domain.exception.InvalidStateNameException
import com.berlin.domain.model.TaskState
import com.berlin.domain.repository.TaskStateRepository

class UpdateTaskStateUseCase(
    private val taskStateRepository: TaskStateRepository,
) {
    operator fun invoke(stateId: String, newStateName: String, projectId: String): String {
        if (!validateStateName(newStateName))
            throw InvalidStateNameException("State Name must not be empty or blank")
        val updatedState = TaskState(
            id = stateId,
            name = newStateName,
            projectId = projectId
        )
        return taskStateRepository.updateState(updatedState)
    }


    private fun validateStateName(stateName: String): Boolean =
        stateName.isNotBlank() && !(stateName.all { it.isDigit() })
}