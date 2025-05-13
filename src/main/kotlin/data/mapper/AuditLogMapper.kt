package com.berlin.data.mapper

import com.berlin.data.dto.AuditLogDto
import com.berlin.domain.model.AuditLog

class AuditLogMapper : EntityMapper<AuditLogDto, AuditLog> {
    override fun mapToDomainModel(auditLogDto: AuditLogDto): AuditLog {
        return AuditLog(
            id = auditLogDto.id,
            timestamp = auditLogDto.timestamp,
            createdByUserId = auditLogDto.createdByUserId,
            auditAction = auditLogDto.auditAction,
            changesDescription = auditLogDto.changesDescription,
            entityType = auditLogDto.entityType,
            entityId = auditLogDto.entityId
        )
    }

    override fun mapToDataModel(auditLog: AuditLog): AuditLogDto {
        return AuditLogDto(
            id = auditLog.id,
            timestamp = auditLog.timestamp,
            createdByUserId = auditLog.createdByUserId,
            auditAction = auditLog.auditAction,
            changesDescription = auditLog.changesDescription,
            entityType = auditLog.entityType,
            entityId = auditLog.entityId
        )
    }
}