package com.berlin.domain.usecase.task_state


import com.berlin.domain.exception.InvalidStateNameException
import com.berlin.domain.repository.TaskStateRepository
import com.berlin.domain.model.TaskState
import com.berlin.domain.usecase.utils.id_generator.IdGeneratorImplementation
import com.berlin.domain.usecase.utils.validation.Validator

class CreateTaskStateUseCase(
    private val taskStateRepository: TaskStateRepository,
    private val idGenerator: IdGeneratorImplementation,
    private val validator: Validator
) {
    operator fun invoke(stateName: String, projectId: String): String {
        if (validator.isValid(stateName)) {
            val newState = TaskState(
                id = idGenerator.generateId(stateName),
                name = stateName,
                projectId = projectId
            )
            return taskStateRepository.addState(newState)
        } else {
            throw InvalidStateNameException("State Name must not be empty or blank")
        }
    }
}
