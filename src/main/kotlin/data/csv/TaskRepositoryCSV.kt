package com.berlin.data.csv

import com.berlin.logic.repositories.TaskRepository
import com.berlin.model.Task
import com.berlin.model.User
import java.io.File
import java.io.FileWriter

class TaskRepositoryCSV(
    private val filePath: String = "tasks.csv",
    private val userRepository: com.berlin.logic.repositories.AuthenticationRepository,
    private val auditRepository: com.berlin.logic.repositories.AuditRepository
) : TaskRepository {

    private val delimiter = ","
    private val file = File(filePath)

    init {
        if (!file.exists()) {
            file.createNewFile()
            FileWriter(file).use { writer ->
                writer.append("id,projectId,title,description,stateId,assignedToId,createdById\n")
            }
        }
    }

    override fun createTask(task: Task): Boolean {
        return try {
            val existingTasks = getAllTasks()
            if (existingTasks.any { it.id == task.id }) {
                return false
            }

            FileWriter(file, true).use { writer ->
                writer.append(taskToCsvLine(task))
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun getTaskById(taskId: String): Task? {
        return getAllTasks().find { it.id == taskId }
    }

    override fun getTasksByProjectId(projectId: String): List<Task> {
        return getAllTasks().filter { it.projectId == projectId }
    }

    override fun updateTask(task: Task): Boolean {
        val tasks = getAllTasks()
        val updatedTasks = tasks.map { if (it.id == task.id) task else it }

        if (tasks.size == updatedTasks.size) {
            return writeAllTasks(updatedTasks)
        }
        return false
    }

    override fun deleteTaskById(taskId: String): Boolean {
        val tasks = getAllTasks()
        val filteredTasks = tasks.filter { it.id != taskId }

        if (tasks.size != filteredTasks.size) {
            return writeAllTasks(filteredTasks)
        }
        return false
    }

    override fun getAssignedUserByTaskId(taskId: String): User? {
        val task = getTaskById(taskId) ?: return null
        return task.assignedTo
    }

    private fun getAllTasks(): List<Task> {
        if (!file.exists()) return emptyList()

        return file.readLines()
            .drop(1) // Skip header
            .filter { it.isNotBlank() }
            .mapNotNull { line ->
                try {
                    csvLineToTask(line)
                } catch (e: Exception) {
                    null
                }
            }
    }

    private fun writeAllTasks(tasks: List<Task>): Boolean {
        return try {
            FileWriter(file).use { writer ->
                writer.append("id,projectId,title,description,stateId,assignedToId,createdById\n")
                tasks.forEach { task ->
                    writer.append(taskToCsvLine(task))
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun taskToCsvLine(task: Task): String {
        val description = task.description?.replace(",", "\\,") ?: ""
        return "${task.id}${delimiter}" +
                "${task.projectId}${delimiter}" +
                "${task.title.replace(",", "\\,")}${delimiter}" +
                "${description}${delimiter}" +
                "${task.stateId}${delimiter}" +
                "${task.assignedTo.id}${delimiter}" +
                "${task.createBy.id}\n"
    }

    private fun csvLineToTask(line: String): Task? {
        val parts = line.split(delimiter)
        if (parts.size < 7) return null

        val id = parts[0]
        val projectId = parts[1]
        val title = parts[2].replace("\\,", ",")
        val description = parts[3].replace("\\,", ",").takeIf { it.isNotBlank() }
        val stateId = parts[4]
        val assignedToId = parts[5]
        val createdById = parts[6]

        val assignedTo = userRepository.getUserById(assignedToId) ?: return null
        val createdBy = userRepository.getUserById(createdById) ?: return null
        val auditLogs = auditRepository.getAuditLogsByTaskId(id)

        return Task(
            id = id,
            projectId = projectId,
            title = title,
            description = description,
            stateId = stateId,
            assignedTo = assignedTo,
            createBy = createdBy,
            auditLogs = auditLogs
        )
    }
}