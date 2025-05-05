package com.berlin.data.state

import com.berlin.data.csv_data_source.CsvDataSource
import com.berlin.domain.exception.InvalidStateException
import com.berlin.domain.model.State
import com.berlin.domain.model.Task
import com.berlin.domain.repository.StateRepository

class StateRepositoryImpl(
    private val stateDataSource: CsvDataSource<State>,
    private val taskDataSource: CsvDataSource<Task>
):StateRepository {
    override fun addState(state: State): Result<String> {
        return Result.failure(InvalidStateException("can not add state"))
    }

    override fun getStatesByProjectId(projectId: String): List<State>? {
        return null
    }

    override fun getTasksByStateId(stateId: String): List<Task>? {
        return null
    }

    override fun deleteState(stateId: String): Result<String> {
        return Result.failure(InvalidStateException("can not delete state"))
    }

    override fun updateState(state: State): Result<String> {
        return Result.failure(InvalidStateException("can not update state"))
    }

    override fun getStateByTaskId(taskId: String): State? {
        return null
    }

    override fun getStateById(stateId: String): State? {
        return null
    }
}