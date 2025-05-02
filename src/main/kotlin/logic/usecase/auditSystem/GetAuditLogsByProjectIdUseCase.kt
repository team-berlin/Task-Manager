package com.berlin.logic.usecase.auditSystem

import com.berlin.domain.model.AuditLog
import com.berlin.logic.repositories.AuditRepository

class GetAuditLogsByProjectIdUseCase(
    private val auditRepository: AuditRepository
) {

    fun getAuditLogsByProjectId(projectId: String): List<AuditLog> {

        if (!validateProjectId(projectId))
            throw IllegalArgumentException("Project ID must not be empty, blank, or purely numeric")

        return auditRepository.getAuditLogsByProjectId(projectId)

    }

    private fun validateProjectId(projectId: String): Boolean =
        projectId.isNotBlank() && !(projectId.all { it.isDigit() })

}