package com.berlin.logic.usecase.project

import com.berlin.logic.generateIdHelper.DefaultIdGenerator
import com.berlin.logic.repositories.ProjectRepository
import com.berlin.model.Project
import jdk.jfr.Description

class CreateProjectUseCase(
    private val projectRepository: ProjectRepository,
    private val defaultIdGenerator: DefaultIdGenerator,
    ) {
        fun createNewProject(projectName: String, description: String?, stateId: List<String>?, taskId: List<String>?):
                Result<String> {
            if (validateProjectName(projectName)) {
                val newProject = Project(
                    id = defaultIdGenerator.generateId(projectName),
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