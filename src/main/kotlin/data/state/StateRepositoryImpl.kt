package com.berlin.data.state

import com.berlin.data.BaseDataSource
import com.berlin.domain.exception.InvalidStateException
import com.berlin.domain.exception.StateNotFoundException
import com.berlin.domain.model.State
import com.berlin.domain.model.Task
import com.berlin.domain.repository.StateRepository
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success

class StateRepositoryImpl(
    private val stateDataSource: BaseDataSource<State>,
    private val taskDataSource: BaseDataSource<Task>,
) : StateRepository {
    override fun addState(state: State): Result<String> =
         if (stateDataSource.write(state))
            success(state.id)
        else
            failure(InvalidStateException("can not add state"))


    override fun getStatesByProjectId(projectId: String): Result<List<State>> =
        success(stateDataSource.getAll().filter { it.projectId == projectId })


    override fun getTasksByStateId(stateId: String): List<Task>? =
        taskDataSource.getAll()
            .filter { it.stateId == stateId }
            .takeIf { it.isNotEmpty() }


    override fun deleteState(stateId: String): Result<String> =
        if (stateDataSource.delete(stateId))
            success(stateId)
        else
            failure(InvalidStateException("can not delete state"))


    override fun updateState(state: State): Result<String> =
        if (stateDataSource.update(state.id, state))
            success(state.id)
        else
            failure(InvalidStateException("can not update state"))


    override fun getStateByTaskId(taskId: String): State? {
        return taskDataSource
            .getById(taskId)
            ?.let { stateDataSource.getById(it.stateId) }
    }

    override fun getStateById(stateId: String): Result<State> =
        stateDataSource.getById(stateId)
            ?.let { success(it) }
            ?: failure(StateNotFoundException(stateId))

    override fun getAllStates(): List<State> {
        return stateDataSource.getAll()
    }
}