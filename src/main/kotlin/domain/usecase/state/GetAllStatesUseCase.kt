package com.berlin.domain.usecase.state

import com.berlin.domain.model.State
import com.berlin.domain.repository.StateRepository

class GetAllStatesUseCase(
    private val stateRepository: StateRepository,
) {

    operator fun invoke(): List<State> {

        return stateRepository.getAllStates()
    }
}
