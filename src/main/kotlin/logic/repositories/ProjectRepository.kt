package com.berlin.logic.repositories

import com.berlin.domain.model.Project


interface ProjectRepository {
    fun createProject(project: Project):Boolean
    fun getProjectById(projectId:String): Project?
    fun getAllProjects():List<Project>
    fun updateProject(project: Project):Boolean
    fun deleteProject(projectId: String):Boolean
}