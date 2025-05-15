package com.berlin.domain.usecase.task_state

import com.berlin.domain.model.TaskState
import com.berlin.domain.repository.TaskStateRepository

class GetAllTaskStatesUseCase(
    private val taskStateRepository: TaskStateRepository,
) {
    operator fun invoke(): List<TaskState> {
        return taskStateRepository.getAllStates()
    }
}