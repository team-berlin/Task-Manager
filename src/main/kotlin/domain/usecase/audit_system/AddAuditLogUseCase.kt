package com.berlin.domain.usecase.audit_system

import com.berlin.domain.usecase.utils.id_generator.IdGenerator
import com.berlin.domain.model.AuditAction
import com.berlin.domain.model.AuditLog
import com.berlin.domain.model.EntityType
import com.berlin.domain.repository.AuditRepository

class AddAuditLogUseCase(
    private val auditRepository: AuditRepository,
    private val idGenerator: IdGenerator
) {

    fun addAuditLog(
        createdByUserId:String,
        auditAction: AuditAction,
        changesDescription: String? = null,
        entityType: EntityType,
        entityId: String,
    ): Result<String> {
        return try {
            val auditLog = AuditLog(
                id = idGenerator.generateId("AUDIT"),
                timestamp = System.currentTimeMillis(),
                createdByUserId=createdByUserId,
                auditAction = auditAction,
                changesDescription = changesDescription,
                entityType = entityType,
                entityId = entityId
            )

            val result = auditRepository.addAuditLog(auditLog)
            if (result.isSuccess) {
                Result.success("Audit log added successfully")
            } else {
                Result.failure(Exception("Audit log failed to add"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Audit log failed to add"))
        }
    }
}