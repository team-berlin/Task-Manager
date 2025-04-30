package com.berlin.logic.usecase.state

import com.berlin.logic.repositories.StateRepository
import com.berlin.logic.repositories.TaskRepository
import com.berlin.model.Task

class GetTasksByStateIdUseCase (
    private val stateRepository: StateRepository,
    private val taskRepository: TaskRepository
) {

    fun getTasksByStateId(stateId: String): List<Task>? {
        if (!validateTaskId(stateId)) throw Exception("Task ID must not be empty or blank")

        if (checkStateExists(stateId)) {
            return stateRepository.getTaskByStateId(stateId)?.takeIf { it.isNotEmpty() }
                ?: throw Exception("No tasks found for state ID $stateId")
        } else {
            throw Exception("State with ID $stateId does not exist")
        }
    }


    private fun checkStateExists(stateId: String): Boolean = taskRepository.getTaskById(stateId) != null

    private fun validateTaskId(stateId: String): Boolean = stateId.isNotBlank() && !(stateId.all { it.isDigit() })
}