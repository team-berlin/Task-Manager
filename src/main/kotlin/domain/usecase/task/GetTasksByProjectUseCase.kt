package com.berlin.domain.usecase.task

import com.berlin.domain.model.Task
import com.berlin.domain.repository.TaskRepository

class GetTasksByProjectUseCase(private val taskRepository: TaskRepository) {
    operator fun invoke(projectId: String): Result<List<Task>> =
        taskRepository.findTasksByProjectId(projectId)
}
