package com.berlin.domain.usecase.project

import com.berlin.domain.model.AuditAction
import com.berlin.domain.model.EntityType
import com.berlin.domain.usecase.utils.IDGenerator.IdGenerator
import com.berlin.domain.repository.ProjectRepository
import com.berlin.domain.model.Project
import com.berlin.domain.usecase.auditSystem.AddAuditLogUseCase
import data.UserCache

class CreateProjectUseCase(
    private val projectRepository: ProjectRepository,
    private val idGenerator: IdGenerator,
    private val addAuditLogUseCase: AddAuditLogUseCase,
    private val cashedUser: UserCache
    ) {
        fun createNewProject(projectName: String, description: String?, stateId: List<String>?, taskId: List<String>?):
                Result<String> {
            if (validateProjectName(projectName)) {
                val newProject = Project(
                    id = idGenerator.generateId(projectName),
                    name = projectName,
                    description = description,
                    statesId = stateId,
                    tasksId = taskId
                )

                val createdProject = projectRepository.createProject(newProject)

                if (createdProject.isSuccess) {
                    addAuditLogUseCase.addAuditLog(
                        createdByUserId = cashedUser.currentUser.id,
                        auditAction = AuditAction.CREATE,
                        entityType = EntityType.PROJECT,
                        entityId = newProject.id,
                    )
                }

                return createdProject
                    .map { "Creation Successfully" }
                    .recover { "Creation Failed" }
            } else {
                throw Exception("Project Name must not be empty or blank")
            }
        }

        private fun validateProjectName(projectName: String): Boolean =
            projectName.isNotBlank() && !(projectName.all { it.isDigit() })
}