package com.berlin.domain.usecase.task

import com.berlin.domain.exception.InvalidTaskStateException
import com.berlin.domain.model.AuditAction
import com.berlin.domain.model.EntityType
import com.berlin.domain.model.Task
import com.berlin.domain.repository.TaskRepository
import com.berlin.domain.usecase.auditSystem.AddAuditLogUseCase

class ChangeTaskStateUseCase(
    private val taskRepository: TaskRepository,
    private val addAuditLogUseCase: AddAuditLogUseCase
) {

    operator fun invoke(taskId: String, newStateId: String): Result<Task> {

        val originalResult = taskRepository.findById(taskId)
        if (originalResult.isFailure) return originalResult
        val original = originalResult.getOrThrow()

        if (!validateStateId(newStateId)) {
            throw InvalidTaskStateException("State id must not be empty, blank, or purely numeric")
        }

        val updated = original.copy(stateId = newStateId)
        val result=taskRepository.update(updated)
        if (result.isSuccess)
            addAuditLogUseCase.addAuditLog(
                createdByUserId = "u1",
                auditAction = AuditAction.UPDATE,
                changesDescription = "Change Task State To $newStateId",
                entityType = EntityType.TASK,
                entityId = taskId
            )
        return result
    }

    private fun validateStateId(stateId: String): Boolean =
        stateId.isNotBlank() && !stateId.all { it.isDigit() }
}
