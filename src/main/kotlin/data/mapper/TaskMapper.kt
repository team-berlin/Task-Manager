package com.berlin.data.mapper

import com.berlin.data.dto.TaskDto
import com.berlin.domain.model.Task

class TaskMapper : EntityMapper<TaskDto, Task> {
    override fun mapToDomainModel(from: TaskDto): Task {
        return Task(
            id = from.id,
            title = from.title,
            projectId = from.projectId,
            description = from.description,
            stateId = from.stateId,
            assignedToUserId = from.assignedToUserId,
            createByUserId = from.createByUserId
        )
    }

    override fun mapToDataModel(from: Task): TaskDto {
        return TaskDto(
            id = from.id,
            title = from.title,
            projectId = from.projectId,
            description = from.description,
            stateId = from.stateId,
            assignedToUserId = from.assignedToUserId,
            createByUserId = from.createByUserId
        )
    }
}