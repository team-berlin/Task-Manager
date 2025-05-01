package com.berlin.domain.usecase.task

import com.berlin.domain.exception.TaskNotFoundException
import com.berlin.domain.repository.TaskRepository

class DeleteTaskUseCase(
    private val taskRepository: TaskRepository,
) {
    operator fun invoke(taskId: String): Result<Unit> {
        if (!validateTaskId(taskId)) throw Exception("Project ID must not be empty or blank")

        if (!checkTaskExists(taskId)) {
            return Result.failure(
                TaskNotFoundException("task with ID $taskId does not exist")
            )
        }
        return taskRepository.delete(taskId)
    }

    private fun validateTaskId(taskId: String): Boolean =
        taskId.isNotBlank() && !(taskId.all { it.isDigit() })

    private fun checkTaskExists(taskId: String): Boolean =
        taskRepository.findById(taskId).isSuccess

}
