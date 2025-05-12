package com.berlin.data.mapper

import com.berlin.data.dto.TaskStateDto
import com.berlin.domain.model.TaskState

class TaskStateMapper : EntityMapper<TaskStateDto, TaskState> {
    override fun mapToDomainModel(taskStateDto: TaskStateDto): TaskState {
        return TaskState(
            id = taskStateDto.id,
            name = taskStateDto.name,
            projectId = taskStateDto.projectId
        )
    }

    override fun mapToDataModel(taskState: TaskState): TaskStateDto {
        return TaskStateDto(
            id = taskState.id,
            name = taskState.name,
            projectId = taskState.projectId
        )
    }
}