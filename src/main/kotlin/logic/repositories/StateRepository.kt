package com.berlin.logic.repositories

import com.berlin.model.State

interface StateRepository {
    fun addState(state: State): Result<String>
    fun getStatesByProjectId(projectId: String):List<State>
    fun deleteState(stateId: String): Result<String>
    fun updateState(state: State): Result<String>
    fun getStateByTaskId(taskId: String): State?
}