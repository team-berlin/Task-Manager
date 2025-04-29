package com.berlin.logic.usecases.task

import com.berlin.logic.repositories.AuditRepository
import com.berlin.logic.repositories.ProjectRepository
import com.berlin.logic.repositories.TaskRepository
import com.berlin.model.AuditAction
import com.berlin.model.AuditLog
import com.berlin.model.EntityType
import com.berlin.model.Task
import com.berlin.model.User
import java.util.UUID

class CreateTaskUseCase(
    private val taskRepository: TaskRepository,
    private val projectRepository: ProjectRepository,
    private val auditRepository: AuditRepository
) {
    fun execute(
        projectId: String,
        title: String,
        description: String?,
        stateId: String,
        assignedTo: User,
        createdBy: User
    ): Boolean {
        val taskId = "TASK-${UUID.randomUUID().toString().substring(0, 8)}"

        val task = Task(
            id = taskId,
            projectId = projectId,
            title = title,
            description = description,
            stateId = stateId,
            assignedTo = assignedTo,
            createBy = createdBy,
            auditLogs = emptyList()
        )

        val created = taskRepository.createTask(task)

        if (created) {
            // Update project's task list
            val project = projectRepository.getProjectById(projectId)
            project?.let {
                val updatedProject = it.copy(
                    tasksId = it.tasksId + taskId
                )
                projectRepository.updateProject(updatedProject)
            }

            // Create audit log
            val auditLog = AuditLog(
                id = "AUDIT-${UUID.randomUUID().toString().substring(0, 8)}",
                timestamp = System.currentTimeMillis(),
                createdBy = createdBy,
                auditAction = AuditAction.CREATE,
                changesDescription = "Task created: $title",
                entityType = EntityType.TASK,
                entityId = taskId
            )
            auditRepository.addAuditLog(auditLog)
        }

        return created
    }
}