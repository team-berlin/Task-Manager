package com.berlin.domain.usecase.task

import com.berlin.domain.model.AuditAction
import com.berlin.domain.model.EntityType
import com.berlin.domain.repository.TaskRepository
import com.berlin.domain.usecase.audit_system.AddAuditLogUseCase
import data.UserCache

class DeleteTaskUseCase(
    private val taskRepository: TaskRepository,
    private val addAuditLogUseCase: AddAuditLogUseCase,
    private val cashedUser: UserCache,
) {
    operator fun invoke(taskId: String) : String {
        if (!validateTaskId(taskId)) {
            throw Exception("Project ID must not be empty or blank")
        }
        taskRepository.deleteTask(taskId)

        addAuditLogUseCase.addAuditLog(
            createdByUserId = cashedUser.currentUser.id,
            auditAction = AuditAction.DELETE,
            entityType = EntityType.TASK,
            entityId = taskId,
        )

        return "Deleted."
    }

    private fun validateTaskId(taskId: String): Boolean = taskId.isNotBlank() && !(taskId.all { it.isDigit() })
}
