package com.berlin.domain.usecase.state

import com.berlin.domain.exception.InvalidTaskIdException
import com.berlin.domain.exception.InvalidTaskStateException
import com.berlin.domain.model.TaskState
import com.berlin.domain.repository.StateRepository
import com.berlin.domain.repository.TaskRepository

class GetStateByTaskIdUseCase(
    private val stateRepository: StateRepository,
    private val taskRepository: TaskRepository
) {

    fun getStateByTaskId(taskId: String): TaskState? {
        if (!validateTaskId(taskId)) throw InvalidTaskIdException("Task ID must not be empty or blank")

        if (checkTaskExists(taskId)) {
            return stateRepository.getStateByTaskId(taskId)
        } else {
            throw InvalidTaskStateException("Task with ID $taskId does not exist")
        }
    }


    private fun checkTaskExists(taskId: String): Boolean = taskRepository.getTaskById(taskId).isSuccess

    private fun validateTaskId(taskId: String): Boolean = taskId.isNotBlank() && !(taskId.all { it.isDigit() })

}