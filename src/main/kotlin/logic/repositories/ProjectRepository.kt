package com.berlin.logic.repositories

import com.berlin.model.Project

interface ProjectRepository {
    fun createProject(project:Project):Boolean
    fun getProjectById(projectId:Int):Project?
    fun getAllProjects():List<Project>
    fun updateProject(project: Project):Boolean
    fun deleteProject(projectId: Int):Boolean
}