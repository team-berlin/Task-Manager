package com.berlin.domain.repository

import com.berlin.domain.model.Task

interface TaskRepository {
    fun createTask(task: Task): Task
    fun updateTask(task: Task): Task
    fun getTaskById(id: String): Task
    fun getTasksByProjectId(projectId: String): List<Task>
    fun deleteTask(id: String)
    fun getAllTasks(): List<Task>
}
