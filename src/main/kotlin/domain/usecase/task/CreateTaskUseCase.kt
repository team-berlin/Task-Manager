package com.berlin.domain.usecase.task

import com.berlin.domain.model.*
import com.berlin.domain.repository.TaskRepository

class CreateTaskUseCase(private val taskRepository: TaskRepository) {

    operator fun invoke(
        projectId: String,
        title: String,
        description: String?,
        stateId: String,
        creator: User,
        assignee: User
    ): Result<Task> {

        val task = Task(
            id = taskRepository.nextId(),
            projectId = projectId,
            title = title,
            description = description,
            stateId = stateId,
            assignedToUserId = assignee.id,
            createByUserId = creator.id
        )

        // Todo: save audio log

        return taskRepository.create(task)
    }
}