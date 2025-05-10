package com.berlin.domain.repository

import com.berlin.domain.model.Project

interface ProjectRepository {
     suspend fun createProject(project:Project): Result<String>
     suspend fun getProjectById(projectId:String): Project?
     suspend fun getAllProjects(): List<Project>?
     suspend fun updateProject(project: Project): Result<String>
     suspend fun deleteProject(projectId: String): Result<String>
}