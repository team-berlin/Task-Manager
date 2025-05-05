package com.berlin.domain.usecase.task

import com.berlin.domain.exception.InvalidTaskTitle
import com.berlin.domain.model.AuditAction
import com.berlin.domain.model.EntityType
import com.berlin.domain.model.Task
import com.berlin.domain.repository.TaskRepository
import com.berlin.domain.usecase.auditSystem.AddAuditLogUseCase

class UpdateTaskUseCase(
    private val taskRepository: TaskRepository,
    private val addAuditLogUseCase: AddAuditLogUseCase
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
            val result=taskRepository.update(updated)
            if (result.isSuccess)
                addAuditLogUseCase.addAuditLog(
                    createdByUserId = updated.createByUserId,
                    auditAction = AuditAction.UPDATE,
                    changesDescription = "Update task assign to ${updated.assignedToUserId} $taskId",
                    entityType = EntityType.TASK,
                    entityId = taskId
                )
            return result
        }
    }

    private fun validateTaskTitle(title: String): Boolean {
        return title.isNotBlank() && !title.all { it.isDigit() }
    }
}
