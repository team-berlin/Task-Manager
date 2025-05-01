package com.berlin.logic.repositories

import com.berlin.model.State
import com.berlin.model.Task

interface StateRepository {
    fun addState(state: State): Result<String>
    fun getStatesByProjectId(projectId: String):List<State>?
    fun getTasksByStateId(stateId: String):List<Task>?
    fun deleteState(stateId: String): Result<String>
    fun updateState(state: State): Result<String>
    fun getStateByTaskId(taskId: String): State?
    fun getStateById(stateId: String): State?
}