package com.berlin.domain.usecase.task

import com.berlin.domain.model.AuditLog
import com.berlin.domain.repository.TaskRepository
import com.berlin.domain.usecase.audit_system.AddAuditLogUseCase
import com.berlin.domain.usecase.utils.isIDValid
import data.UserCache

class DeleteTaskUseCase(
    private val taskRepository: TaskRepository,
    private val addAuditLogUseCase: AddAuditLogUseCase,
    private val cashedUser: UserCache,
) {
    operator fun invoke(taskId: String) : String {
        if (isIDValid(taskId).not()) {
            throw Exception("Project ID must not be empty or blank")
        }
        taskRepository.deleteTask(taskId)

        addAuditLogUseCase(
            createdByUserId = cashedUser.currentUser.id,
            auditAction = AuditLog.AuditAction.DELETE,
            entityType = AuditLog.EntityType.TASK,
            entityId = taskId,
        )

        return "Deleted."
    }

}
