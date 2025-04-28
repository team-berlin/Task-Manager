package com.berlin.data

import com.berlin.logic.repositories.StateRepository
import com.berlin.model.State

class CsvStateRepository:StateRepository {
    override fun createState(state: State): Boolean {
        return false
    }

    override fun getStatesByProjectId(projectId: String): List<State> {
        return emptyList()
    }

    override fun deleteState(stateId: String):Boolean {
        return false
    }

    override fun updateState(state: State): Boolean {
        return false
    }

    override fun getStateByTaskId(taskId: String): State? {
        return null
    }
}