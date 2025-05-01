package com.berlin.helper

import com.berlin.model.*

fun generateAuditLog(
    id: String = "A1",
    timestamp: Long = System.currentTimeMillis(),
    createdBy: User = User("u1", "TestUser", "ffkjkuyu", UserRole.ADMIN),
    action: AuditAction = AuditAction.CREATE,
    changesDescription: String? = "Created something",
    entityType: EntityType = EntityType.TASK,
    entityId: String = "G2"
): AuditLog {
    return AuditLog(
        id = id,
        timestamp = timestamp,
        createdBy = createdBy,
        auditAction = action,
        changesDescription = changesDescription,
        entityType = entityType,
        entityId = entityId
    )
}
