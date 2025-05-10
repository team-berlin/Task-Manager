package com.berlin.domain.usecase.project

import com.berlin.domain.model.AuditAction
import com.berlin.domain.model.EntityType
import com.berlin.domain.repository.ProjectRepository
import com.berlin.domain.model.Project
import com.berlin.domain.usecase.audit_system.AddAuditLogUseCase
import data.UserCache

class UpdateProjectUseCase (
    private val projectRepository: ProjectRepository,
    private val addAuditLogUseCase: AddAuditLogUseCase,
    private val cashedUser: UserCache
) {
    fun updateProject(project: Project): Result<String> {
        if(!validateProjectName(project.name))
            throw Exception("Project Name must not be empty or blank")

        val updatedProject = projectRepository.updateProject(project)

        if (updatedProject.isSuccess) {
            addAuditLogUseCase.addAuditLog(
                createdByUserId = cashedUser.currentUser.id,
                auditAction = AuditAction.UPDATE,
                entityType = EntityType.PROJECT,
                entityId = project.id,
            )
        }

        return updatedProject
            .map { "Updated Successfully" }
            .recover { "Update Failed" }
    }


    private fun validateProjectName(projectName: String): Boolean =
        projectName.isNotBlank() && !(projectName.all { it.isDigit() })
}