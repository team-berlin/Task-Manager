package com.berlin.data.csv

import com.berlin.logic.repositories.ProjectRepository
import com.berlin.model.Project
import java.io.File
import java.io.FileWriter

class ProjectRepositoryCSV(
    private val filePath: String = "projects.csv"
) : ProjectRepository {

    private val delimiter = ","
    private val file = File(filePath)

    init {
        if (!file.exists()) {
            file.createNewFile()
            FileWriter(file).use { writer ->
                writer.append("id,name,description,statesId,tasksId\n")
                // إضافة مشروع افتراضي للاختبار
                writer.append("PROJ-001,Default Project,A default project,STATE-001;STATE-002;STATE-003,\n")
            }
        }
    }

    override fun createProject(project: Project): Boolean {
        return try {
            val existingProjects = getAllProjects()
            if (existingProjects.any { it.id == project.id }) {
                return false
            }

            FileWriter(file, true).use { writer ->
                writer.append(projectToCsvLine(project))
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun getProjectById(projectId: String): Project? {
        return getAllProjects().find { it.id == projectId }
    }

    override fun getAllProjects(): List<Project> {
        if (!file.exists()) return emptyList()

        return file.readLines()
            .drop(1) // تخطي العنوان
            .filter { it.isNotBlank() }
            .mapNotNull { line ->
                try {
                    csvLineToProject(line)
                } catch (e: Exception) {
                    null
                }
            }
    }

    override fun updateProject(project: Project): Boolean {
        val projects = getAllProjects()
        val updatedProjects = projects.map { if (it.id == project.id) project else it }

        if (projects.size == updatedProjects.size) {
            return writeAllProjects(updatedProjects)
        }
        return false
    }

    override fun deleteProject(projectId: String): Boolean {
        val projects = getAllProjects()
        val filteredProjects = projects.filter { it.id != projectId }

        if (projects.size != filteredProjects.size) {
            return writeAllProjects(filteredProjects)
        }
        return false
    }

    private fun writeAllProjects(projects: List<Project>): Boolean {
        return try {
            FileWriter(file).use { writer ->
                writer.append("id,name,description,statesId,tasksId\n")
                projects.forEach { project ->
                    writer.append(projectToCsvLine(project))
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun projectToCsvLine(project: Project): String {
        val description = project.description?.replace(",", "\\,") ?: ""
        val statesIdStr = project.statesId.joinToString(";")
        val tasksIdStr = project.tasksId.joinToString(";")

        return "${project.id}${delimiter}" +
                "${project.name.replace(",", "\\,")}${delimiter}" +
                "${description}${delimiter}" +
                "${statesIdStr}${delimiter}" +
                "${tasksIdStr}\n"
    }

    private fun csvLineToProject(line: String): Project? {
        val parts = line.split(delimiter)
        if (parts.size < 5) return null

        val id = parts[0]
        val name = parts[1].replace("\\,", ",")
        val description = parts[2].replace("\\,", ",").takeIf { it.isNotBlank() }
        val statesId = if (parts[3].isNotBlank()) parts[3].split(";") else emptyList()
        val tasksId = if (parts[4].isNotBlank()) parts[4].split(";") else emptyList()

        return Project(
            id = id,
            name = name,
            description = description,
            statesId = statesId,
            tasksId = tasksId
        )
    }
}