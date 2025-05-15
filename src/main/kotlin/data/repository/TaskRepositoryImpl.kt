package com.berlin.data.repository

import com.berlin.data.BaseDataSource
import com.berlin.data.dto.TaskDto
import com.berlin.data.mapper.TaskMapper
import com.berlin.domain.exception.InvalidTaskException
import com.berlin.domain.exception.TaskNotFoundException
import com.berlin.domain.model.Task
import com.berlin.domain.repository.TaskRepository

class TaskRepositoryImpl(
    private val baseDataSource: BaseDataSource<TaskDto>,
    private val taskMapper: TaskMapper
) : TaskRepository {

    override fun createTask(task: Task): Task {
        val taskDto = taskMapper.mapToDataModel(task)
        if (baseDataSource.write(taskDto))
            return task
        else throw InvalidTaskException("some thing went error")
    }

    override fun updateTask(task: Task): Task {
        val taskDto = taskMapper.mapToDataModel(task)
        if (!baseDataSource.update(taskDto.id, taskDto))
            throw InvalidTaskException("some thing went error")
        return task
    }

    override fun getTaskById(id: String): Task {
        return taskMapper.mapToDomainModel(
            baseDataSource.getById(id) ?: throw TaskNotFoundException(id)
        )
    }

    override fun getTasksByProjectId(projectId: String): List<Task> {
        val tasks = baseDataSource.getAll().map {
            taskMapper.mapToDomainModel(it)
        }
        return tasks.filter { it.projectId == projectId }
    }

    override fun deleteTask(id: String) {
        if (!baseDataSource.delete(id))
            throw TaskNotFoundException(id)
    }

    override fun getAllTasks(): List<Task> {
         return baseDataSource.getAll().map {
             taskMapper.mapToDomainModel(it)
         }
    }
}