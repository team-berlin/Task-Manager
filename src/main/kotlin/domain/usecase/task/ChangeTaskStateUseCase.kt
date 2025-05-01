package com.berlin.domain.usecase.task

import com.berlin.domain.exception.InvalidTaskStateException
import com.berlin.domain.model.Task
import com.berlin.domain.repository.TaskRepository

class ChangeTaskStateUseCase(
    private val taskRepository: TaskRepository
) {

    operator fun invoke(taskId: String, newStateId: String): Result<Task> {

        val originalResult = taskRepository.findById(taskId)
        if (originalResult.isFailure) return originalResult
        val original = originalResult.getOrThrow()

        if (!validateStateId(newStateId)) {
            throw InvalidTaskStateException("State id must not be empty, blank, or purely numeric")
        }

        val updated = original.copy(stateId = newStateId)
        return taskRepository.update(updated)
    }

    private fun validateStateId(stateId: String): Boolean =
        stateId.isNotBlank() && !stateId.all { it.isDigit() }
}
