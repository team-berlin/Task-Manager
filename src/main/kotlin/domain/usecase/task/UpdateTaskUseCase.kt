package com.berlin.domain.usecase.task

import com.berlin.domain.exception.InvalidTaskTitle
import com.berlin.domain.model.AuditAction
import com.berlin.domain.model.EntityType
import com.berlin.domain.model.Task
import com.berlin.domain.repository.TaskRepository
import com.berlin.domain.usecase.audit_system.AddAuditLogUseCase
import data.UserCache

class UpdateTaskUseCase(
    private val taskRepository: TaskRepository,
    private val addAuditLogUseCase: AddAuditLogUseCase,
    private val cashedUser: UserCache
) {

    operator fun invoke(
        taskId: String,
        title: String? = null,
        description: String? = null,
        assignedToUserId: String? = null,
    ): Result<Task> {

        val originalResult = taskRepository.getTaskById(taskId)
        if (originalResult.isFailure) return originalResult
        val original = originalResult.getOrThrow()

        val updated = original.copy(
            title = title ?: original.title,
            description = description ?: original.description,
            assignedToUserId = assignedToUserId ?: original.assignedToUserId
        )

        if (!validateTaskTitle(updated.title.trim()))
            throw InvalidTaskTitle("task title must be not empty or plank")

        val updatedTask = taskRepository.createTask(updated)

        if (updatedTask.isSuccess) {
            addAuditLogUseCase.addAuditLog(
                createdByUserId = cashedUser.currentUser.id,
                auditAction = AuditAction.UPDATE,
                entityType = EntityType.TASK,
                entityId = updated.id,
            )
        }

        return updatedTask
    }

    private fun validateTaskTitle(title: String): Boolean {
        return title.isNotBlank() && !title.all { it.isDigit() }
    }
}
