package com.berlin.data.memory

import com.berlin.domain.exception.*
import com.berlin.domain.model.Task
import com.berlin.domain.repository.TaskRepository
import com.berlin.data.DummyData.tasks
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success
import kotlin.runCatching

class TaskRepositoryInMemory : TaskRepository {

    override fun create(task: Task): Result<Task> = runCatching {
        tasks += task
        task
    }

    override fun update(task: Task): Result<Task> = runCatching {
        val idx = tasks.indexOfFirst { it.id == task.id }
        tasks[idx] = task
        task
    }

    override fun findById(id: String): Result<Task> =
        tasks.firstOrNull { it.id == id }
            ?.let(::success)
            ?: failure(TaskNotFoundException(id))

    override fun findTasksByProjectId(projectId: String): Result<List<Task>> =
        success(tasks.filter { it.projectId == projectId })

    override fun delete(id: String): Result<Unit> = runCatching {
        val removed = tasks.removeIf { it.id == id }
        if (!removed) throw TaskNotFoundException(id)
    }

    override fun nextId(): String = (tasks.size + 1).toString()

    override fun getAllTasks(): List<Task> {
        return tasks
    }
}
