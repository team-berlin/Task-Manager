package com.berlin.data.mapper

import com.berlin.data.dto.AuditLogDto
import com.berlin.domain.model.AuditLog

class AuditLogMapper : EntityMapper<AuditLogDto, AuditLog> {
    override fun mapToDomainModel(from: AuditLogDto): AuditLog {
        return AuditLog(
            id = from.id,
            timestamp = from.timestamp,
            createdByUserId = from.createdByUserId,
            auditAction = from.auditAction,
            changesDescription = from.changesDescription,
            entityType = from.entityType,
            entityId = from.entityId
        )
    }

    override fun mapToDataModel(from: AuditLog): AuditLogDto {
        return AuditLogDto(
            id = from.id,
            timestamp = from.timestamp,
            createdByUserId = from.createdByUserId,
            auditAction = from.auditAction,
            changesDescription = from.changesDescription,
            entityType = from.entityType,
            entityId = from.entityId
        )
    }
}