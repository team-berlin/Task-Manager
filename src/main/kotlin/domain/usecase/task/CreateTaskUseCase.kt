package com.berlin.domain.usecase.task

import com.berlin.domain.exception.InvalidTaskTitle
import com.berlin.domain.exception.TaskAlreadyExistsException
import com.berlin.domain.model.AuditLog
import com.berlin.domain.model.Task
import com.berlin.domain.repository.TaskRepository
import com.berlin.domain.usecase.audit_system.AddAuditLogUseCase
import com.berlin.domain.usecase.utils.id_generator.IdGenerator
import com.berlin.domain.usecase.utils.validation.Validator

class CreateTaskUseCase(
    private val taskRepository: TaskRepository,
    private val defaultIdGenerator: IdGenerator,
    private val addAuditLogUseCase: AddAuditLogUseCase,
    private val validator: Validator
) {
    operator fun invoke(
        projectId: String,
        title: String,
        description: String?,
        stateId: String,
        createByUserId: String,
        assignedToUserId: String,
    ): Task {
        if (validator.isValid(title.trim())) {
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

            val createdTask = taskRepository.createTask(newTask)

            addAuditLogUseCase(
                createdByUserId = createByUserId,
                auditAction = AuditLog.AuditAction.CREATE,
                entityType = AuditLog.EntityType.TASK,
                entityId = newTask.id,
            )

            return createdTask
        } else {
            throw InvalidTaskTitle("task title must be not empty or plank")
        }
    }

    private fun validateUniqueTask(taskId: String): Boolean {
        return try {
            taskRepository.getTaskById(taskId)
            true
        } catch (_: Exception){
            false
        }
    }
}
