package com.berlin.domain.usecase.task

import com.berlin.domain.exception.InvalidTaskStateException
import com.berlin.domain.model.AuditLog
import com.berlin.domain.model.Task
import com.berlin.domain.repository.TaskRepository
import com.berlin.domain.usecase.audit_system.AddAuditLogUseCase
import com.berlin.domain.usecase.utils.validation.Validator
import data.UserCache

class ChangeTaskStateUseCase(
    private val taskRepository: TaskRepository,
    private val addAuditLogUseCase: AddAuditLogUseCase,
    private val cashedUser: UserCache,
    private val validator: Validator
) {

    operator fun invoke(taskId: String, newStateId: String): Task {

        val original = taskRepository.getTaskById(taskId)

        if (!validator.isValid(newStateId)) {
            throw InvalidTaskStateException("State id must not be empty, blank, or purely numeric")
        }

        val updated = original.copy(stateId = newStateId)
        val updatedTask = taskRepository.updateTask(updated)

        addAuditLogUseCase(
            createdByUserId = cashedUser.currentUser.id,
            auditAction = AuditLog.AuditAction.UPDATE,
            entityType = AuditLog.EntityType.TASK,
            entityId = updated.id,
        )

        return updatedTask
    }
}
