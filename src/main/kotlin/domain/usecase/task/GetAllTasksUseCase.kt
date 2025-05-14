package com.berlin.domain.usecase.task

import com.berlin.domain.model.Task
import com.berlin.domain.repository.TaskRepository

class GetAllTasksUseCase(
    private val taskRepository: TaskRepository,
) {

    operator fun invoke(): List<Task> {
        return taskRepository.getAllTasks()
    }
}
