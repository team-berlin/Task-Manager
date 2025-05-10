package com.berlin.domain.usecase.auditSystem

import com.berlin.domain.model.AuditLog
import com.berlin.domain.repository.AuditRepository

class GetAuditLogsByProjectIdUseCase(
    private val auditRepository: AuditRepository
) {

    suspend fun getAuditLogsByProjectId(projectId: String): List<AuditLog> {

        if (!validateProjectId(projectId))
            throw IllegalArgumentException("Project ID must not be empty, blank, or purely numeric")

        return auditRepository.getAuditLogsByProjectId(projectId)

    }

    private fun validateProjectId(projectId: String): Boolean =
        projectId.isNotBlank() && !(projectId.all { it.isDigit() })

}