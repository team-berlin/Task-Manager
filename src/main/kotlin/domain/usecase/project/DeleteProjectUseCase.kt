package com.berlin.domain.usecase.project

import com.berlin.domain.model.AuditAction
import com.berlin.domain.model.EntityType
import com.berlin.domain.repository.ProjectRepository
import com.berlin.domain.usecase.audit_system.AddAuditLogUseCase
import data.UserCache

class DeleteProjectUseCase (
    private val projectRepository: ProjectRepository,
    private val addAuditLogUseCase: AddAuditLogUseCase,
    private val cashedUser: UserCache
) {
    fun deleteProject(projectId: String): Result<String> {

        if(!validateProjectId(projectId))
            throw Exception("Project ID must not be empty or blank")

        if (!checkProjectExists(projectId)) {
            return Result.failure(
                Exception("Project with ID $projectId does not exist")
            )
        }

        val deletedProject = projectRepository.deleteProject(projectId)

        if (deletedProject.isSuccess) {
            addAuditLogUseCase.addAuditLog(
                createdByUserId = cashedUser.currentUser.id,
                auditAction = AuditAction.DELETE,
                entityType = EntityType.PROJECT,
                entityId = projectId,
            )
        }

        return deletedProject
            .map { "Deleted Successfully" }
            .recover { "Deletion Failed" }
    }

    private fun validateProjectId(projectId: String): Boolean =
        projectId.isNotBlank() && !(projectId.all { it.isDigit() })

    private fun checkProjectExists(projectId: String): Boolean =
        projectRepository.getProjectById(projectId) != null
}