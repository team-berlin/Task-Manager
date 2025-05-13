package com.berlin.domain.usecase.project

import com.berlin.domain.exception.InvalidProjectIdException
import com.berlin.domain.exception.ProjectNotFoundException
import com.berlin.domain.model.AuditLog
import com.berlin.domain.repository.ProjectRepository
import com.berlin.domain.usecase.audit_system.AddAuditLogUseCase
import data.UserCache

class DeleteProjectUseCase(
    private val projectRepository: ProjectRepository,
    private val addAuditLogUseCase: AddAuditLogUseCase,
    private val cashedUser: UserCache,
) {
    operator fun invoke(projectId: String): String {

        if (!validateProjectId(projectId)) {
            throw InvalidProjectIdException("Project ID must not be empty or blank")
        }

        if (!checkProjectExists(projectId)) {
            throw ProjectNotFoundException("Project with ID $projectId does not exist")
        }

        val deletedProject = projectRepository.deleteProject(projectId)

        addAuditLogUseCase(
            createdByUserId = cashedUser.currentUser.id,
            auditAction = AuditLog.AuditAction.DELETE,
            entityType = AuditLog.EntityType.PROJECT,
            entityId = projectId,
        )

        return deletedProject
    }

    private fun validateProjectId(projectId: String): Boolean =
        projectId.isNotBlank() && !(projectId.all { it.isDigit() })

    private fun checkProjectExists(projectId: String): Boolean = projectRepository.getProjectById(projectId) != null
}