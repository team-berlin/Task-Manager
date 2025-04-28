package com.berlin.data

import com.berlin.logic.repositories.StateRepository
import com.berlin.model.State

class CsvStateRepository:StateRepository {
    override fun createState(state: State): Boolean {
        return false
    }

    override fun getStatesByProjectId(projectId: Int): List<State> {
        return emptyList()
    }

    override fun deleteState(stateId: Int):Boolean {
        return false
    }
}