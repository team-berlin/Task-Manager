package com.berlin.domain.logic.repositories

import com.berlin.domain.model.State

interface StateRepository {
    fun createState(state: State):Boolean
    fun getStatesByProjectId(projectId:String):List<State>
    fun deleteState(stateId:String):Boolean
    fun updateState(state: State):Boolean
    fun getStateByTaskId(taskId:String): State?
}