package com.berlin.data.dto

import com.berlin.domain.model.AuditLog
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty

data class AuditLogDto(
    @BsonId val id: String,
    @BsonProperty("timestamp") val timestamp: Long,
    @BsonProperty("created_by_user_id") val createdByUserId: String,
    @BsonProperty("audit_action") val auditAction: AuditLog.AuditAction,
    @BsonProperty("changes_description") val changesDescription: String?,
    @BsonProperty("entity_type") val entityType: AuditLog.EntityType,
    @BsonProperty("entity_id") val entityId: String
)