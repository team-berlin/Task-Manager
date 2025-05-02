package com.berlin.domain.usecase.task

import com.berlin.domain.exception.InvalidTaskTitle
import com.berlin.domain.exception.TaskAlreadyExistsException
import com.berlin.domain.helper.IdGeneratorImplementation
import com.berlin.domain.model.Task
import com.berlin.domain.repository.TaskRepository

class CreateTaskUseCase(
    private val taskRepository: TaskRepository,
    private val defaultIdGenerator: IdGeneratorImplementation
) {
    operator fun invoke(
        projectId: String,
        title: String,
        description: String?,
        stateId: String,
        createByUserId: String,
        assignedToUserId: String,
    ): Result<Task> {
        if (validateTaskTitle(title.trim())) {
            val newTask = Task(
                id = defaultIdGenerator.generateId(title.trim()),
                projectId = projectId,
                title = title.trim(),
                description = description,
                stateId = stateId,
                assignedToUserId = assignedToUserId,
                createByUserId = createByUserId
            )
            if (!validateUniqueTask(newTask.id)) {
                throw TaskAlreadyExistsException("Task with id= ${newTask.id} and title = ${newTask.title} already exists")
            }
            return taskRepository.create(newTask)
        } else {
            throw InvalidTaskTitle("task title must be not empty or plank")
        }
    }

    private fun validateTaskTitle(title: String): Boolean {
        return title.isNotBlank() && !title.any { it.isDigit() }
    }

    private fun validateUniqueTask(taskId: String): Boolean {
        return (taskRepository.getAllTasks().none { it.id == taskId })
    }
}
