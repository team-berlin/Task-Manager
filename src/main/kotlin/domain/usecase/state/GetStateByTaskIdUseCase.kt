package com.berlin.domain.usecase.state

import com.berlin.domain.model.State
import com.berlin.domain.repository.StateRepository
import com.berlin.domain.repository.TaskRepository

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


    private fun checkTaskExists(taskId: String): Boolean = taskRepository.findById(taskId) != null

    private fun validateTaskId(taskId: String): Boolean = taskId.isNotBlank() && !(taskId.all { it.isDigit() })

}