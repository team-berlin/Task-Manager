package com.berlin.domain.usecase.project

import com.berlin.domain.model.AuditLog
import com.berlin.domain.model.Project
import com.berlin.domain.repository.ProjectRepository
import com.berlin.domain.usecase.audit_system.AddAuditLogUseCase
import com.berlin.domain.usecase.utils.id_generator.IdGenerator
import data.UserCache

class CreateProjectUseCase(
    private val projectRepository: ProjectRepository,
    private val idGenerator: IdGenerator,
    private val addAuditLogUseCase: AddAuditLogUseCase,
    private val cashedUser: UserCache,
) {
    fun createNewProject(
        projectName: String,
        description: String?,
        stateId: List<String>?,
        taskId: List<String>?,
    ): String {
        if (validateProjectName(projectName)) {
            val newProject = Project(
                id = idGenerator.generateId(projectName),
                title = projectName,
                description = description,
                statesId = stateId,
                tasksId = taskId
            )

            val createdProject = projectRepository.createProject(newProject)

            addAuditLogUseCase.addAuditLog(
                createdByUserId = cashedUser.currentUser.id,
                auditAction = AuditLog.AuditAction.CREATE,
                entityType = AuditLog.EntityType.PROJECT,
                entityId = newProject.id,
            )

            return createdProject
        } else {
            throw Exception("Project Name must not be empty or blank")
        }
    }

    private fun validateProjectName(projectName: String): Boolean =
        projectName.isNotBlank() && !(projectName.all { it.isDigit() })
}