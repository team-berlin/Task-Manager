package com.berlin.logic.usecase.auditSystem

import com.berlin.domain.model.AuditAction
import com.berlin.domain.model.AuditLog
import com.berlin.domain.model.EntityType
import com.berlin.domain.model.User
import com.berlin.logic.generateIdHelper.IdGenerator
import com.berlin.logic.repositories.AuditRepository

class AddAuditLogUseCase(
    private val auditRepository: AuditRepository,
    private val idGenerator: IdGenerator

) {

    fun addAuditLog(
        createdBy: User,
        auditAction: AuditAction,
        changesDescription: String,
        entityType: EntityType,
        entityId: String
    ): Result<String> {
        return try {
            val id = idGenerator.generateId("AUDIT")
            val auditLog = AuditLog(
                id = id,
                timestamp = System.currentTimeMillis(),
                createdByUserId = createdBy.id,
                auditAction = auditAction,
                changesDescription = changesDescription,
                entityType = entityType,
                entityId = entityId
            )

            val result = auditRepository.addAuditLog(auditLog)
            if (result.isSuccess) {
                result
            } else {
                Result.failure(Exception("Audit log failed to add"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Audit log failed to add"))
        }
    }
}