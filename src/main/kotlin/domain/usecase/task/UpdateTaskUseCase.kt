package com.berlin.domain.usecase.task

import com.berlin.domain.exception.InvalidTaskTitle
import com.berlin.domain.exception.TaskNotFoundException
import com.berlin.domain.model.Task
import com.berlin.domain.repository.TaskRepository
import com.berlin.data.DummyData.tasks

class UpdateTaskUseCase(
    private val taskRepository: TaskRepository,
) {

    operator fun invoke(
        taskId: String,
        title: String? = null,
        description: String? = null,
        assignedToUserId: String? = null,
    ): Result<Task> {

        val originalResult = taskRepository.findById(taskId)
        if (originalResult.isFailure) return originalResult
        val original = originalResult.getOrThrow()

        val updated = original.copy(
            title = title ?: original.title,
            description = description ?: original.description,
            assignedToUserId = assignedToUserId ?: original.assignedToUserId
        )
        if (!validateTaskTitle(updated.title.trim())) {
            throw InvalidTaskTitle("task title must be not empty or plank")
        } else {
            return taskRepository.update(updated)
        }
    }

    private fun validateTaskTitle(title: String): Boolean {
        return title.isNotBlank() && !title.all { it.isDigit() }
    }
}
