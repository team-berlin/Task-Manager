package com.berlin.data.state

import com.berlin.data.BaseDataSource
import com.berlin.domain.exception.InvalidStateException
import com.berlin.domain.model.State
import com.berlin.domain.model.Task
import com.berlin.domain.repository.StateRepository

class StateRepositoryImpl(
    private val stateDataSource: BaseDataSource<State>,
    private val taskDataSource: BaseDataSource<Task>
):StateRepository {
    override fun addState(state: State): Result<String> {
        return if (stateDataSource.write(state))
            Result.success(state.id)
        else
            Result.failure(InvalidStateException("can not add state"))
    }

    override fun getStatesByProjectId(projectId: String): List<State>? {
        return stateDataSource.getAll()
            .filter { it.projectId == projectId }
            .takeIf { it.isNotEmpty() }
    }

    override fun getTasksByStateId(stateId: String): List<Task>? {
        return taskDataSource.getAll()
            .filter { it.stateId == stateId }
            .takeIf { it.isNotEmpty() }
    }

    override fun deleteState(stateId: String): Result<String> {
        return if (stateDataSource.delete(stateId))
            Result.success(stateId)
        else
            Result.failure(InvalidStateException("can not delete state"))
    }

    override fun updateState(state: State): Result<String> {
        return if (stateDataSource.update(state.id,state))
            Result.success(state.id)
        else
            Result.failure(InvalidStateException("can not update state"))
    }

    override fun getStateByTaskId(taskId: String): State? {
       return taskDataSource
           .getById(taskId)
           ?.let { stateDataSource.getById(it.stateId) }
    }

    override fun getStateById(stateId: String): State? {
        return stateDataSource.getById(stateId)
    }
}