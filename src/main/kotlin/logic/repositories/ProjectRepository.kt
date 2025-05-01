package com.berlin.logic.repositories

import com.berlin.model.Project

interface ProjectRepository {
    fun createProject(project:Project): Result<String>
    fun getProjectById(projectId:String): Project?
    fun getAllProjects(): List<Project>?
    fun updateProject(project: Project): Result<String>
    fun deleteProject(projectId: String): Result<String>
}