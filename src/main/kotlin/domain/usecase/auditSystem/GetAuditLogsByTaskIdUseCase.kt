package com.berlin.domain.usecase.auditSystem

import com.berlin.domain.model.AuditLog
import com.berlin.domain.repository.AuditRepository

class GetAuditLogsByTaskIdUseCase(
    private val auditRepository: AuditRepository
) {

    fun getAuditLogsByTaskId(taskId:String):List<AuditLog> {

        if (!validateTaskId(taskId))
            throw IllegalArgumentException("Task ID must not be empty, blank, or purely numeric")

        return auditRepository.getAuditLogsByProjectId(taskId)

    }

    private fun validateTaskId(projectId: String): Boolean =
        projectId.isNotBlank() && !(projectId.all { it.isDigit() })
    }