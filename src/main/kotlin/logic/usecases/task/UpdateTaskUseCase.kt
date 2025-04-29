package com.berlin.logic.usecases.task

import com.berlin.logic.repositories.AuditRepository
import com.berlin.logic.repositories.TaskRepository
import com.berlin.model.AuditAction
import com.berlin.model.AuditLog
import com.berlin.model.EntityType
import com.berlin.model.User
import java.util.UUID

class UpdateTaskUseCase(
    private val taskRepository: TaskRepository,
    private val auditRepository: AuditRepository
) {
    fun execute(
        taskId: String,
        title: String,
        description: String?,
        stateId: String,
        assignedTo: User,
        updatedBy: User
    ): Boolean {
        val existingTask = taskRepository.getTaskById(taskId) ?: return false

        val updatedTask = existingTask.copy(
            title = title,
            description = description,
            stateId = stateId,
            assignedTo = assignedTo
        )

        val updated = taskRepository.updateTask(updatedTask)

        if (updated) {
            val changes = mutableListOf<String>()

            if (existingTask.title != title) {
                changes.add("title changed from '${existingTask.title}' to '$title'")
            }

            if (existingTask.description != description) {
                changes.add("description updated")
            }

            if (existingTask.stateId != stateId) {
                changes.add("state changed from '${existingTask.stateId}' to '$stateId'")
            }

            if (existingTask.assignedTo.id != assignedTo.id) {
                changes.add("assigned user changed from '${existingTask.assignedTo.id}' to '${assignedTo.id}'")
            }

            val changesDescription = changes.joinToString(", ")

            val auditLog = AuditLog(
                id = "AUDIT-${UUID.randomUUID().toString().substring(0, 8)}",
                timestamp = System.currentTimeMillis(),
                createdBy = updatedBy,
                auditAction = AuditAction.UPDATE,
                changesDescription = "Task updated: $changesDescription",
                entityType = EntityType.TASK,
                entityId = taskId
            )
            auditRepository.addAuditLog(auditLog)
        }

        return updated
    }
}