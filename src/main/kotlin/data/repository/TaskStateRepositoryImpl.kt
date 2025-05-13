package com.berlin.data.repository

import com.berlin.data.BaseDataSource
import com.berlin.data.dto.TaskDto
import com.berlin.data.dto.TaskStateDto
import com.berlin.data.mapper.TaskMapper
import com.berlin.data.mapper.TaskStateMapper
import com.berlin.domain.exception.InvalidStateException
import com.berlin.domain.exception.StateNotFoundException
import com.berlin.domain.model.TaskState
import com.berlin.domain.model.Task
import com.berlin.domain.repository.TaskStateRepository

class TaskStateRepositoryImpl(
    private val stateDataSource: BaseDataSource<TaskStateDto>,
    private val taskDataSource: BaseDataSource<TaskDto>,
    private val taskStateMapper: TaskStateMapper,
    private val taskMapper: TaskMapper
) : TaskStateRepository {

    override fun addState(state: TaskState): String {
        val stateDto = taskStateMapper.mapToDataModel(state)
        if (stateDataSource.write(stateDto))
            return "State created successfully"
        else
            throw InvalidStateException("can not add state")
    }

    override fun getStatesByProjectId(projectId: String): List<TaskState> {
        val taskStates = stateDataSource.getAll().map {
            taskStateMapper.mapToDomainModel(it)
        }
        return taskStates.filter { it.projectId == projectId }
    }

    override fun getTasksByStateId(stateId: String): List<Task>? {
        val tasks = taskDataSource.getAll().map {
            taskMapper.mapToDomainModel(it)
        }
        return tasks
            .filter { it.stateId == stateId }
            .takeIf { it.isNotEmpty() }
    }

    override fun deleteState(stateId: String): String {
        if (stateDataSource.delete(stateId))
            return "Deleted Successfully"
        else
            throw InvalidStateException("can not delete state")
    }

    override fun updateState(state: TaskState): String {
        val stateDto = taskStateMapper.mapToDataModel(state)
        if (stateDataSource.update(state.id, stateDto))
            return "Updated Successfully"
        else
            throw InvalidStateException("can not update state")
    }

    override fun getStateByTaskId(taskId: String): TaskState? {
        val taskDto = taskDataSource.getById(taskId)
        return taskDto?.let {
            taskStateMapper.mapToDomainModel(
                stateDataSource.getById(taskDto.stateId) ?: throw StateNotFoundException(taskDto.stateId)
            )
        }
    }

    override fun getStateById(stateId: String): TaskState {
        return stateDataSource.getById(stateId)
            ?.let { taskStateMapper.mapToDomainModel(it) }
            ?: throw StateNotFoundException(stateId)
    }

    override fun getAllStates(): List<TaskState> {
        return stateDataSource.getAll().map {
            taskStateMapper.mapToDomainModel(it)
        }
    }
}