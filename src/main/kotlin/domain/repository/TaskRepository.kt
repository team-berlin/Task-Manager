package com.berlin.domain.repository

import com.berlin.domain.model.Task

interface TaskRepository {
    fun create(task: Task): Result<Task>
    fun update(task: Task): Result<Task>
    fun findById(id: String): Result<Task>
    fun findTasksByProjectId(projectId: String): Result<List<Task>>
    fun delete(id: String): Result<Unit>
    fun nextId(): String
    fun getAllTasks(): List<Task>
}
