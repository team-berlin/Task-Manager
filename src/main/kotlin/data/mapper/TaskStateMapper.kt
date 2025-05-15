package com.berlin.data.mapper

import com.berlin.data.dto.TaskStateDto
import com.berlin.domain.model.TaskState

class TaskStateMapper : EntityMapper<TaskStateDto, TaskState> {
    override fun mapToDomainModel(from: TaskStateDto): TaskState {
        return TaskState(
            id = from.id,
            name = from.name,
            projectId = from.projectId
        )
    }

    override fun mapToDataModel(from: TaskState): TaskStateDto {
        return TaskStateDto(
            id = from.id,
            name = from.name,
            projectId = from.projectId
        )
    }
}