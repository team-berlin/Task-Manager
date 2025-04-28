package com.berlin.data

import com.berlin.logic.repositories.TaskRepository
import com.berlin.model.Task

class CsvTaskRepository:TaskRepository {
    override fun createTask(projectId: Int, task: Task): Boolean {
        return false
    }

    override fun getTaskById(taskId: Int): Task? {
        return null
    }

    override fun getTasksByProjectId(projectId: Int): List<Task> {
        return emptyList()
    }

    override fun updateTask(task: Task): Boolean {
        return false
    }

    override fun deleteTask(task: Task): Boolean {
        return false
    }
}