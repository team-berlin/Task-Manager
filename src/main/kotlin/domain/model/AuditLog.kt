package com.berlin.domain.model

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty

data class AuditLog(
    @BsonId val id: String,
    val timestamp: Long,
    @BsonProperty("created_by_user_id") val createdByUserId: String,
    @BsonProperty("audit_action") val auditAction: AuditAction,
    @BsonProperty("changes_description") val changesDescription: String?,
    @BsonProperty("entity_type") val entityType: EntityType,
    @BsonProperty("entity_id") val entityId: String
) {
    enum class AuditAction {
        CREATE, UPDATE, DELETE
    }

    enum class EntityType {
        PROJECT,TASK
    }
}