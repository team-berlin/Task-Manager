package com.berlin.domain.repository

import com.berlin.domain.model.TaskState
import com.berlin.domain.model.Task

interface StateRepository {
    fun addState(state: TaskState): Result<String>
    fun getStatesByProjectId(projectId: String): Result<List<TaskState>>
    fun getTasksByStateId(stateId: String):List<Task>?
    fun deleteState(stateId: String): Result<String>
    fun updateState(state: TaskState): Result<String>
    fun getStateByTaskId(taskId: String): TaskState?
    fun getStateById(stateId: String): Result<TaskState>
    fun getAllStates(): List<TaskState>
}