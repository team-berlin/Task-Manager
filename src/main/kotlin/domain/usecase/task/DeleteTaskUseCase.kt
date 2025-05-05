package com.berlin.domain.usecase.task

import com.berlin.domain.exception.TaskNotFoundException
import com.berlin.domain.model.AuditAction
import com.berlin.domain.model.EntityType
import com.berlin.domain.repository.TaskRepository
import com.berlin.domain.usecase.auditSystem.AddAuditLogUseCase

class DeleteTaskUseCase(
    private val taskRepository: TaskRepository,
    private val addAuditLogUseCase: AddAuditLogUseCase
) {
    operator fun invoke(taskId: String): Result<Unit> {
        if (!validateTaskId(taskId)) throw Exception("Project ID must not be empty or blank")

        if (!checkTaskExists(taskId)) {
            return Result.failure(
                TaskNotFoundException("task with ID $taskId does not exist")
            )
        }
        val result=taskRepository.delete(taskId)
        if (result.isSuccess)
            addAuditLogUseCase.addAuditLog(
                createdByUserId = "ui",
                auditAction = AuditAction.DELETE,
                changesDescription = "Delete task  ${taskId}",
                entityType = EntityType.TASK,
                entityId = taskId
            )
        return result
    }

    private fun validateTaskId(taskId: String): Boolean =
        taskId.isNotBlank() && !(taskId.all { it.isDigit() })

    private fun checkTaskExists(taskId: String): Boolean =
        taskRepository.findById(taskId).isSuccess

}
