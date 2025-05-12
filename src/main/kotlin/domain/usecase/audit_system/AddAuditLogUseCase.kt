package com.berlin.domain.usecase.audit_system

import com.berlin.domain.model.AuditAction
import com.berlin.domain.model.AuditLog
import com.berlin.domain.model.EntityType
import com.berlin.domain.repository.AuditRepository
import com.berlin.domain.usecase.utils.id_generator.IdGenerator

class AddAuditLogUseCase(
    private val auditRepository: AuditRepository,
    private val idGenerator: IdGenerator,
) {

    fun addAuditLog(
        createdByUserId: String,
        auditAction: AuditAction,
        changesDescription: String? = null,
        entityType: EntityType,
        entityId: String,
    ): String {
        val auditLog = AuditLog(
            id = idGenerator.generateId("AUDIT"),
            timestamp = System.currentTimeMillis(),
            createdByUserId = createdByUserId,
            auditAction = auditAction,
            changesDescription = changesDescription,
            entityType = entityType,
            entityId = entityId
        )

        auditRepository.addAuditLog(auditLog)
        return "Audit log added successfully"
    }
}