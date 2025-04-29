package com.berlin.logic.usecases.task

import com.berlin.logic.repositories.AuditRepository
import com.berlin.logic.repositories.ProjectRepository
import com.berlin.logic.repositories.TaskRepository
import com.berlin.model.AuditAction
import com.berlin.model.AuditLog
import com.berlin.model.EntityType
import com.berlin.model.User
import java.util.UUID

class DeleteTaskUseCase(
    private val taskRepository: TaskRepository,
    private val projectRepository: ProjectRepository,
    private val auditRepository: AuditRepository
) {
    fun execute(taskId: String, deletedBy: User): Boolean {
        val task = taskRepository.getTaskById(taskId) ?: return false

        val deleted = taskRepository.deleteTaskById(taskId)

        if (deleted) {
            // Update project's task list
            val project = projectRepository.getProjectById(task.projectId)
            project?.let {
                val updatedProject = it.copy(
                    tasksId = it.tasksId.filter { id -> id != taskId }
                )
                projectRepository.updateProject(updatedProject)
            }

            // Create audit log
            val auditLog = AuditLog(
                id = "AUDIT-${UUID.randomUUID().toString().substring(0, 8)}",
                timestamp = System.currentTimeMillis(),
                createdBy = deletedBy,
                auditAction = AuditAction.DELETE,
                changesDescription = "Task deleted: ${task.title}",
                entityType = EntityType.TASK,
                entityId = taskId
            )
            auditRepository.addAuditLog(auditLog)
        }

        return deleted
    }
}