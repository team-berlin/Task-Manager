package com.berlin.domain.usecase.task

import com.berlin.domain.model.Task
import com.berlin.domain.model.User
import com.berlin.domain.repository.TaskRepository
import kotlin.Result

class UpdateTaskUseCase(
    private val taskRepository: TaskRepository
) {

    operator fun invoke(
        taskId: String,
        title: String? = null,
        description: String? = null,
        assignee: User? = null
    ): Result<Task> {

        val originalResult = taskRepository.findById(taskId)
        if (originalResult.isFailure) return originalResult
        val original = originalResult.getOrThrow()

        val updated = original.copy(
            title       = title       ?: original.title,
            description = description ?: original.description
        )

        return taskRepository.update(updated)
    }
}
