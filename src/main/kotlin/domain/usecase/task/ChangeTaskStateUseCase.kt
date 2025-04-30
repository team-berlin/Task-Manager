package com.berlin.domain.usecase.task

import com.berlin.data.memory.TaskRepositoryInMemory
import com.berlin.domain.model.Task

class ChangeTaskStateUseCase(
    private val taskRepository: TaskRepositoryInMemory
) {
    operator fun invoke(taskId: String, newStateId: String): Result<Task> =
        taskRepository.findById(taskId).mapCatching { original ->
            taskRepository.update(original.copy(stateId = newStateId)).getOrThrow()
        }
}
