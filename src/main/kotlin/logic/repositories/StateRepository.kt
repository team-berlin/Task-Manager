package com.berlin.logic.repositories

import com.berlin.model.State

interface StateRepository {
    fun createState(state: State):Boolean
    fun getStatesByProjectId(projectId:Int):List<State>
    fun deleteState(stateId:Int):Boolean
}