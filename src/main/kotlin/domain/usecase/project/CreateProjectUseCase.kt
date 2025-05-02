package com.berlin.domain.usecase.project

import com.berlin.domain.helper.IdGenerator
import com.berlin.domain.repository.ProjectRepository
import com.berlin.domain.model.Project

class CreateProjectUseCase(
    private val projectRepository: ProjectRepository,
    private val idGenerator: IdGenerator,
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
                return projectRepository.createProject(newProject)
                    .map { "Creation Successfully" }
                    .recover { "Creation Failed" }
            } else {
                throw Exception("Project Name must not be empty or blank")
            }
        }

        private fun validateProjectName(projectName: String): Boolean =
            projectName.isNotBlank() && !(projectName.all { it.isDigit() })
}