package com.berlin.domain.usecase.project

import com.berlin.domain.exception.InvalidProjectException
import com.berlin.domain.model.AuditLog
import com.berlin.domain.model.Project
import com.berlin.domain.repository.ProjectRepository
import com.berlin.domain.usecase.audit_system.AddAuditLogUseCase
import com.berlin.domain.usecase.utils.validation.Validator
import data.UserCache

class UpdateProjectUseCase(
    private val projectRepository: ProjectRepository,
    private val addAuditLogUseCase: AddAuditLogUseCase,
    private val cashedUser: UserCache,
    private val validator: Validator
) {
    operator fun invoke(project: Project): String {
        if (!validator.isValid(project.title)) throw InvalidProjectException("Project Name must not be empty or blank")

        val updatedProject = projectRepository.updateProject(project)

        addAuditLogUseCase(
            createdByUserId = cashedUser.currentUser.id,
            auditAction = AuditLog.AuditAction.UPDATE,
            entityType = AuditLog.EntityType.PROJECT,
            entityId = project.id,
        )

        return updatedProject
    }
}