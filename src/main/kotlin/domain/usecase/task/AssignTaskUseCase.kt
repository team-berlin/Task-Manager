package com.berlin.domain.usecase.task

import com.berlin.domain.exception.InvalidAssigneeException
import com.berlin.domain.model.Task
import com.berlin.domain.repository.TaskRepository

class AssignTaskUseCase(
    private val taskRepository: TaskRepository
) {

    operator fun invoke(taskId: String, newAssigneeId: String): Result<Task> {

        val originalResult = taskRepository.findById(taskId)
        if (originalResult.isFailure) {
            return originalResult
        }
        val original = originalResult.getOrThrow()

        if (!validateAssignee(newAssigneeId)) {
            throw InvalidAssigneeException("Assignee must have a non-blank id")
        }

        val updated = original.copy(assignedToUserId = newAssigneeId)
        return taskRepository.update(updated)
    }

    private fun validateAssignee(id: String): Boolean =
        id.isNotBlank()
}
