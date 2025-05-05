package com.berlin.domain.repository

import com.berlin.domain.model.State
import com.berlin.domain.model.Task

interface StateRepository {
    suspend fun addState(state: State): Result<String>
    suspend fun getStatesByProjectId(projectId: String):List<State>?
    suspend fun getTasksByStateId(stateId: String):List<Task>?
    suspend fun deleteState(stateId: String): Result<String>
    suspend fun updateState(state: State): Result<String>
    suspend fun getStateByTaskId(taskId: String): State?
    suspend fun getStateById(stateId: String): State?
}