package com.berlin.domain.usecase.task_state

import com.berlin.domain.exception.InvalidTaskIdException
import com.berlin.domain.model.TaskState
import com.berlin.domain.repository.TaskStateRepository
import com.berlin.domain.usecase.utils.isIDValid

class GetTaskStateByTaskIdUseCase(
    private val taskStateRepository: TaskStateRepository,
) {

    operator fun invoke(taskId: String): TaskState? {
        if (isIDValid(taskId).not()) {
            throw InvalidTaskIdException("Task ID must not be empty or blank")

        } else {
            return taskStateRepository.getStateByTaskId(taskId)
        }
    }


}