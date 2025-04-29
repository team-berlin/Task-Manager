package com.berlin.logic.usecases.task

import com.berlin.logic.repositories.AuditRepository
import com.berlin.logic.repositories.TaskRepository
import com.berlin.model.AuditAction
import com.berlin.model.AuditLog
import com.berlin.model.EntityType
import com.berlin.model.User
import java.util.UUID

class AssignTaskUseCase(
    private val taskRepository: TaskRepository,
    private val auditRepository: AuditRepository
) {
    fun execute(taskId: String, assignedTo: User, assignedBy: User): Boolean {
        val task = taskRepository.getTaskById(taskId) ?: return false

        val previousAssignee = task.assignedTo

        if (previousAssignee.id == assignedTo.id) {
            return true // Already assigned to this user
        }

        val updatedTask = task.copy(assignedTo = assignedTo)

        val updated = taskRepository.updateTask(updatedTask)

        if (updated) {
            val auditLog = AuditLog(
                id = "AUDIT-${UUID.randomUUID().toString().substring(0, 8)}",
                timestamp = System.currentTimeMillis(),
                createdBy = assignedBy,
                auditAction = AuditAction.UPDATE,
                changesDescription = "Task reassigned from ${previousAssignee.id} to ${assignedTo.id}",
                entityType = EntityType.TASK,
                entityId = taskId
            )
            auditRepository.addAuditLog(auditLog)
        }

        return updated
    }
}