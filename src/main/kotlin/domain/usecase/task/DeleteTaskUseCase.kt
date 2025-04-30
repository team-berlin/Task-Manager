package com.berlin.domain.usecase.task

import com.berlin.domain.repository.TaskRepository

class DeleteTaskUseCase(
    private val taskRepository: TaskRepository
) {
    operator fun invoke(taskId: String): Result<Unit> =
        taskRepository.delete(taskId)
}
