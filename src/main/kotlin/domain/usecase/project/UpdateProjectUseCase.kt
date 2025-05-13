package com.berlin.domain.usecase.project

import com.berlin.domain.exception.InvalidProjectException
import com.berlin.domain.model.AuditLog
import com.berlin.domain.model.Project
import com.berlin.domain.repository.ProjectRepository
import com.berlin.domain.usecase.audit_system.AddAuditLogUseCase
import data.UserCache

class UpdateProjectUseCase(
    private val projectRepository: ProjectRepository,
    private val addAuditLogUseCase: AddAuditLogUseCase,
    private val cashedUser: UserCache,
) {
    operator fun invoke(project: Project): String {
        if (!validateProjectName(project.title)) throw InvalidProjectException("Project Name must not be empty or blank")

        val updatedProject = projectRepository.updateProject(project)

        addAuditLogUseCase(
            createdByUserId = cashedUser.currentUser.id,
            auditAction = AuditLog.AuditAction.UPDATE,
            entityType = AuditLog.EntityType.PROJECT,
            entityId = project.id,
        )

        return updatedProject
    }

    private fun validateProjectName(projectName: String): Boolean =
        projectName.isNotBlank() && !(projectName.all { it.isDigit() })
}