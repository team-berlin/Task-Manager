package com.berlin.logic.usecase

import com.berlin.logic.repositories.StateRepository
import com.berlin.model.State

class StateUseCase(
    private val stateRepository: StateRepository
) {
    fun createNewState(state: State): Boolean {
        return false
    }


}