package com.berlin.domain.repository

import com.berlin.domain.model.Task

interface TaskRepository {
    fun createTask(task: Task): Result<Task>
    fun updateTask(task: Task): Result<Task>
    fun getTaskById(id: String): Result<Task>
    fun getTasksByProjectId(projectId: String): Result<List<Task>>
    fun deleteTask(id: String): Result<Unit>
    fun getAllTasks(): List<Task>
}
