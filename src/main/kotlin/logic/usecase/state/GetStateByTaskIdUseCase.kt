package com.berlin.logic.usecase.state

import com.berlin.logic.repositories.StateRepository
import com.berlin.logic.repositories.TaskRepository
import com.berlin.domain.model.State

class GetStateByTaskIdUseCase(
    private val stateRepository: StateRepository,
    private val taskRepository: TaskRepository
) {

    fun getStateByTaskId(taskId: String): State? {
        if (!validateTaskId(taskId)) throw Exception("Task ID must not be empty or blank")

        if (checkTaskExists(taskId)) {
            return stateRepository.getStateByTaskId(taskId)
        } else {
            throw Exception("State with ID $taskId does not exist")
        }
    }


    private fun checkTaskExists(taskId: String): Boolean = taskRepository.getTaskById(taskId) != null

    private fun validateTaskId(taskId: String): Boolean = taskId.isNotBlank() && !(taskId.all { it.isDigit() })

}