package com.berlin.domain.usecase.audit_system

import com.berlin.domain.exception.InvalidUserIdException
import com.berlin.domain.model.AuditLog
import com.berlin.domain.repository.AuditRepository
import com.berlin.domain.usecase.utils.validation.Validator

class GetAuditLogsByUserIdUseCase(
    private val auditRepository: AuditRepository,
    private val validator: Validator
) {

    operator fun invoke(userId: String): List<AuditLog> {

        if (!validator.isValid(userId))
            throw InvalidUserIdException("User ID must not be empty, blank, or purely numeric")

        return auditRepository.getAuditLogsByUserId(userId)

    }
}