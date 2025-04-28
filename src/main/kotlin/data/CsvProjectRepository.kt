package com.berlin.data

import com.berlin.logic.repositories.ProjectRepository
import com.berlin.model.Project

class CsvProjectRepository:ProjectRepository {
    override fun createProject(project: Project): Boolean {
        return false
    }

    override fun getProjectById(projectId: String): Project? {
        return null
    }

    override fun getAllProjects(): List<Project> {
        return emptyList()
    }

    override fun updateProject(project: Project):Boolean {
        return false
    }

    override fun deleteProject(projectId: String): Boolean {
        return false
    }
}