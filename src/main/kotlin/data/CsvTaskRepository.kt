package com.berlin.data

import com.berlin.logic.repositories.TaskRepository
import com.berlin.model.Task
import com.berlin.model.User

class CsvTaskRepository:TaskRepository {
    override fun createTask(task: Task): Boolean {
        return false
    }

    override fun getTaskById(taskId: String): Task? {
        return null
    }

    override fun getTasksByProjectId(projectId: String): List<Task> {
        return emptyList()
    }

    override fun updateTask(task: Task): Boolean {
        return false
    }

    override fun deleteTaskById(taskId: String): Boolean {
        return false
    }

    override fun getAssignedUserByTaskId(taskId: String): User? {
        return null
    }
}