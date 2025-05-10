package com.berlin.domain.usecase.state

import com.berlin.domain.exception.InvalidTaskIdException
import com.berlin.domain.exception.InvalidTaskStateException
import com.berlin.domain.model.State
import com.berlin.domain.repository.StateRepository
import com.berlin.domain.repository.TaskRepository

class GetStateByTaskIdUseCase(
    private val stateRepository: StateRepository,
    private val taskRepository: TaskRepository
) {

    suspend fun getStateByTaskId(taskId: String): State? {
        if (!validateTaskId(taskId)) throw InvalidTaskIdException("Task ID must not be empty or blank")

        if (checkTaskExists(taskId)) {
            return stateRepository.getStateByTaskId(taskId)
        } else {
            throw InvalidTaskStateException("Task with ID $taskId does not exist")
        }
    }


    private suspend fun checkTaskExists(taskId: String): Boolean = taskRepository.findById(taskId) != null

    private fun validateTaskId(taskId: String): Boolean = taskId.isNotBlank() && !(taskId.all { it.isDigit() })

}