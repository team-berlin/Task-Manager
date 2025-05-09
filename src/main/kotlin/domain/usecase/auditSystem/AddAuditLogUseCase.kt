package com.berlin.domain.usecase.auditSystem

import com.berlin.domain.usecase.utils.IDGenerator.IdGenerator
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
        changesDescription: String,
        entityType: EntityType,
        entityId: String,
        timestamp: Long = System.currentTimeMillis()
    ): Result<String> {
        return try {
            val id = idGenerator.generateId("AUDIT")
            val auditLog = AuditLog(
                id = id,
                timestamp = timestamp,
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