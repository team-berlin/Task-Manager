package com.berlin.data.mapper

import com.berlin.data.dto.TaskDto
import com.berlin.domain.model.Task

class TaskMapper : EntityMapper<TaskDto, Task> {
    override fun mapToDomainModel(taskDto: TaskDto): Task {
        return Task(
            id = taskDto.id,
            title = taskDto.title,
            projectId = taskDto.projectId,
            description = taskDto.description,
            stateId = taskDto.stateId,
            assignedToUserId = taskDto.assignedToUserId,
            createByUserId = taskDto.createByUserId
        )
    }

    override fun mapToDataModel(task: Task): TaskDto {
        return TaskDto(
            id = task.id,
            title = task.title,
            projectId = task.projectId,
            description = task.description,
            stateId = task.stateId,
            assignedToUserId = task.assignedToUserId,
            createByUserId = task.createByUserId
        )
    }
}