package com.berlin.domain.usecase.audit_system

import com.berlin.domain.exception.InvalidProjectIdException
import com.berlin.domain.model.AuditLog
import com.berlin.domain.repository.AuditRepository
import com.berlin.domain.usecase.utils.validation.Validator


class GetAuditLogsByProjectIdUseCase(
    private val auditRepository: AuditRepository,
    private val validator: Validator
) {

    operator fun invoke(projectId: String): List<AuditLog> {

        if (!validator.isValid(projectId))
            throw InvalidProjectIdException("Project ID must not be empty, blank, or purely numeric")

        return auditRepository.getAuditLogsByProjectId(projectId)
    }
}