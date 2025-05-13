package com.berlin.domain.repository

import com.berlin.domain.model.TaskState
import com.berlin.domain.model.Task

interface TaskStateRepository {
    fun addState(state: TaskState): String
    fun getStatesByProjectId(projectId: String): List<TaskState>
    fun getTasksByStateId(stateId: String):List<Task>?
    fun deleteState(stateId: String): String
    fun updateState(state: TaskState): String
    fun getStateByTaskId(taskId: String): TaskState?
    fun getStateById(stateId: String): TaskState
    fun getAllStates(): List<TaskState>
}