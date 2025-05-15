package com.berlin.domain.usecase.audit_system

import com.berlin.domain.exception.InvalidProjectIdException
import com.berlin.domain.model.AuditLog
import com.berlin.domain.repository.AuditRepository
import com.berlin.domain.usecase.utils.isIDValid

class GetAuditLogsByProjectIdUseCase(
    private val auditRepository: AuditRepository
) {

    operator fun invoke(projectId: String): List<AuditLog> {

        if (isIDValid(projectId).not())
            throw InvalidProjectIdException("Project ID must not be empty, blank, or purely numeric")

        return auditRepository.getAuditLogsByProjectId(projectId)
    }


}