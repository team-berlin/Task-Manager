package com.berlin.helper

import com.berlin.domain.model.AuditLog

fun generateAuditLog(
    id: String = "A1",
    timestamp: Long = System.currentTimeMillis(),
    createdBy: String = "u1",
    action: AuditAction = AuditAction.CREATE,
    changesDescription: String? = "Created something",
    entityType: EntityType = EntityType.TASK,
    entityId: String = "G2"
): AuditLog {
    return AuditLog(
        id = id,
        timestamp = timestamp,
        createdByUserId = createdBy,
        auditAction = action,
        changesDescription = changesDescription,
        entityType = entityType,
        entityId = entityId
    )
}
