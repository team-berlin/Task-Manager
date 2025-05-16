package com.berlin.domain.usecase.audit_system

import com.berlin.domain.exception.InvalidTaskIdException
import com.berlin.domain.model.AuditLog
import com.berlin.domain.repository.AuditRepository
import com.berlin.domain.usecase.utils.validation.Validator

class GetAuditLogsByTaskIdUseCase(
    private val auditRepository: AuditRepository,
    private val validator: Validator
) {

    operator fun invoke(taskId: String): List<AuditLog> {

        if (!validator.isValid(taskId))
            throw InvalidTaskIdException("Task ID must not be empty, blank, or purely numeric")

        return auditRepository.getAuditLogsByTaskId(taskId)
    }
}