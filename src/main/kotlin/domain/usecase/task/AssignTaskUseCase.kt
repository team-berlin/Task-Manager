package com.berlin.domain.usecase.task

import com.berlin.domain.model.Task
import com.berlin.domain.model.User
import com.berlin.domain.repository.TaskRepository

class AssignTaskUseCase(
    private val taskRepository: TaskRepository
) {
    operator fun invoke(taskId: String, newAssignee: User): Result<Task> =
        taskRepository.findById(taskId).mapCatching { original ->
            taskRepository.update(original.copy(assignedToUserId = newAssignee.id)).getOrThrow()
        }
}