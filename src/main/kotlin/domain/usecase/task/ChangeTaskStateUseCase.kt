package com.berlin.domain.usecase.task

import com.berlin.domain.exception.InvalidTaskStateException
import com.berlin.domain.model.AuditAction
import com.berlin.domain.model.EntityType
import com.berlin.domain.model.Task
import com.berlin.domain.repository.TaskRepository
import com.berlin.domain.usecase.audit_system.AddAuditLogUseCase
import data.UserCache

class ChangeTaskStateUseCase(
    private val taskRepository: TaskRepository,
    private val addAuditLogUseCase: AddAuditLogUseCase,
    private val cashedUser: UserCache
) {

    operator fun invoke(taskId: String, newStateId: String): Result<Task> {

        val originalResult = taskRepository.getTaskById(taskId)
        if (originalResult.isFailure) return originalResult
        val original = originalResult.getOrThrow()

        if (!validateStateId(newStateId)) {
            throw InvalidTaskStateException("State id must not be empty, blank, or purely numeric")
        }

        val updated = original.copy(stateId = newStateId)
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

    private fun validateStateId(stateId: String): Boolean =
        stateId.isNotBlank() && !stateId.all { it.isDigit() }
}
