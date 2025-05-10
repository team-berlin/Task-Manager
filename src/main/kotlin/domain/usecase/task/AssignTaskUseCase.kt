package com.berlin.domain.usecase.task

import com.berlin.domain.exception.InvalidAssigneeException
import com.berlin.domain.model.AuditAction
import com.berlin.domain.model.EntityType
import com.berlin.domain.model.Task
import com.berlin.domain.repository.TaskRepository
import com.berlin.domain.usecase.audit_system.AddAuditLogUseCase
import data.UserCache

class AssignTaskUseCase(
    private val taskRepository: TaskRepository,
    private val addAuditLogUseCase: AddAuditLogUseCase,
    private val cashedUser: UserCache
) {

    operator fun invoke(taskId: String, newAssigneeId: String): Result<Task> {

        val originalResult = taskRepository.getTaskById(taskId)
        if (originalResult.isFailure) {
            return originalResult
        }
        val original = originalResult.getOrThrow()

        if (!validateAssignee(newAssigneeId)) {
            throw InvalidAssigneeException("Assignee must have a non-blank id")
        }

        val updated = original.copy(assignedToUserId = newAssigneeId)

        val updatedTask = taskRepository.updateTask(updated)

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

    private fun validateAssignee(id: String): Boolean =
        id.isNotBlank()
}
