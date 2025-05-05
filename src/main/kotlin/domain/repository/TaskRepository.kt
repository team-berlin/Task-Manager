package com.berlin.domain.repository

import com.berlin.domain.model.Task

interface TaskRepository {
    suspend fun create(task: Task): Result<Task>
    suspend fun update(task: Task): Result<Task>
    suspend fun findById(id: String): Result<Task>
    suspend fun findTasksByProjectId(projectId: String): Result<List<Task>>
    suspend fun delete(id: String): Result<Unit>
    suspend fun getAllTasks(): List<Task>
}
