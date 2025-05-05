package com.berlin.data.state

import com.berlin.data.BaseDataSource
import com.berlin.domain.exception.InvalidStateException
import com.berlin.domain.exception.StateNotFoundException
import com.berlin.domain.model.State
import com.berlin.domain.model.Task
import com.berlin.domain.repository.StateRepository
import kotlin.Result.Companion.success

class StateRepositoryImpl(
    val baseDataSource: BaseDataSource<State>,
) : StateRepository {

    override fun addState(state: State): Result<State> = runCatching {
        if (baseDataSource.write(state)) state else throw InvalidStateException("some thing went error")
    }

    override fun getStatesByProjectId(projectId: String): Result<List<State>> =
        success(baseDataSource.getAll().filter { it.projectId == projectId })


    override fun getTasksByStateId(stateId: String): List<Task>? {
        TODO("Not yet implemented")
    }

    override fun deleteState(stateId: String): Result<Unit> = runCatching {
        if (!baseDataSource.delete(stateId))
            throw StateNotFoundException(stateId)
    }

    override fun updateState(state: State): Result<String> {
        TODO("Not yet implemented")
    }

    override fun getStateByTaskId(taskId: String): State? {
        TODO("Not yet implemented")
    }

    override fun getStateById(stateId: String): State? {
        TODO("Not yet implemented")
    }

    override fun getAllStates(): List<State> {
        return baseDataSource.getAll()
    }

}