package com.berlin.data.memory

import com.berlin.data.BaseDataSource
import com.berlin.domain.exception.InvalidTaskException
import com.berlin.domain.exception.TaskNotFoundException
import com.berlin.domain.model.Task
import com.berlin.domain.repository.TaskRepository
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success

class TaskRepositoryImpl(
    val baseDataSource: BaseDataSource<Task>,
) : TaskRepository {

    override suspend fun create(task: Task): Result<Task> = runCatching {
        if (baseDataSource.write(task)) task else throw InvalidTaskException("some thing went error")
    }

    override suspend fun update(task: Task): Result<Task> = runCatching {
        if (!baseDataSource.update(task.id, task))
            throw InvalidTaskException("some thing went error")
        task
    }

    override suspend fun findById(id: String): Result<Task> =
        baseDataSource.getById(id)
            ?.let { success(it) }
            ?: failure(TaskNotFoundException(id))


    override suspend fun findTasksByProjectId(projectId: String): Result<List<Task>> =
        success(baseDataSource.getAll().filter { it.projectId == projectId })

    override suspend fun delete(id: String): Result<Unit> = runCatching {
        if (!baseDataSource.delete(id))
            throw TaskNotFoundException(id)
    }

    override suspend fun getAllTasks(): List<Task> {
         return baseDataSource.getAll()
    }
}
