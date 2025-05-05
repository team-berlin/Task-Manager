package com.berlin.domain.usecase.task

import com.berlin.domain.exception.InvalidAssigneeException
import com.berlin.domain.model.AuditAction
import com.berlin.domain.model.EntityType
import com.berlin.domain.model.Task
import com.berlin.domain.repository.TaskRepository
import com.berlin.domain.usecase.auditSystem.AddAuditLogUseCase

class AssignTaskUseCase(
    private val taskRepository: TaskRepository,
    private val addAuditLogUseCase: AddAuditLogUseCase
) {

    operator fun invoke(taskId: String, newAssigneeId: String): Result<Task> {

        val originalResult = taskRepository.findById(taskId)
        if (originalResult.isFailure) {
            return originalResult
        }
        val original = originalResult.getOrThrow()

        if (!validateAssignee(newAssigneeId)) {
            throw InvalidAssigneeException("Assignee must have a non-blank id")
        }

        val updated = original.copy(assignedToUserId = newAssigneeId)
        val result=taskRepository.update(updated)
        if (result.isSuccess)
            addAuditLogUseCase.addAuditLog(
            createdByUserId = "u1",
            auditAction = AuditAction.UPDATE,
            changesDescription = "Assign Task To $newAssigneeId",
            entityType = EntityType.TASK,
            entityId = taskId
        )
        return result
    }

    private fun validateAssignee(id: String): Boolean =
        id.isNotBlank()
}
