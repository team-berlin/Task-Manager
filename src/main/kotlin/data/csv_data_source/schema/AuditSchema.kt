package com.berlin.data.csv_data_source.schema

import com.berlin.data.AuditLogIndex
import com.berlin.data.dto.AuditLogDto
import com.berlin.domain.model.AuditLog

class AuditSchema(
    override val fileName: String, override val header: List<String>
) : BaseSchema<AuditLogDto> {

    init {
        require(
            fileName.isNotEmpty() && header.size == NUMBER_OF_ATTRIBUTES
        )
    }

    override fun toRow(entity: AuditLogDto): List<String> {
        return if (checkAuditLogIsNotValid(entity)) emptyList()
        else auditLogToStringsList(entity)
    }

    override fun fromRow(row: List<String>): AuditLogDto? {
        return if (checkRowIsNotValidAuditLog(row)) null
        else stringsListToAuditLog(row)
    }

    override fun getId(entity: AuditLogDto): String? {
        return entity.id.ifEmpty { null }
    }

    private fun auditLogToStringsList(auditLog: AuditLogDto): List<String> {
        return listOf(
            auditLog.id,
            auditLog.timestamp.toString(),
            auditLog.createdByUserId,
            auditLog.auditAction.toString(),
            auditLog.changesDescription ?: "",
            auditLog.entityType.toString(),
            auditLog.entityId
        )
    }

    private fun stringsListToAuditLog(row: List<String>): AuditLogDto {
        return AuditLogDto(
            id = row[AuditLogIndex.ID],
            timestamp = row[AuditLogIndex.TIMES_TAMP].toLong() ,
            createdByUserId = row[AuditLogIndex.CREATE_BY],
            auditAction = stringToAuditAction(row[AuditLogIndex.AUDIT_ACTION]),
            changesDescription = row[AuditLogIndex.CHANGES_DESCRIPTION].ifEmpty { null },
            entityType = stringToEntityType(row[AuditLogIndex.ENTITY_TYPE]),
            entityId = row[AuditLogIndex.ENTITY_ID]
        )
    }

    private fun checkRowIsNotValidAuditLog(row: List<String>): Boolean {
        return (row[AuditLogIndex.ID].isEmpty() ||
                row[AuditLogIndex.TIMES_TAMP].isEmpty() ||
                row[AuditLogIndex.CREATE_BY].isEmpty() ||
                row[AuditLogIndex.AUDIT_ACTION].isEmpty() ||
                row[AuditLogIndex.ENTITY_ID].isEmpty() ||
                row[AuditLogIndex.AUDIT_ACTION] !in enumValues<AuditLog.AuditAction>().map { it.name } ||
                row[AuditLogIndex.ENTITY_TYPE] !in enumValues<AuditLog.EntityType>().map { it.name }
                )
    }

    private fun stringToAuditAction(auditAction: String): AuditLog.AuditAction {
        return when (auditAction) {
            AuditLog.AuditAction.CREATE.toString() -> AuditLog.AuditAction.CREATE
            AuditLog.AuditAction.UPDATE.toString() -> AuditLog.AuditAction.UPDATE
            AuditLog.AuditAction.DELETE.toString() -> AuditLog.AuditAction.DELETE
            else -> AuditLog.AuditAction.CREATE
        }
    }

    private fun stringToEntityType(entityType: String): AuditLog.EntityType {
        return when (entityType) {
            AuditLog.EntityType.TASK.toString() -> AuditLog.EntityType.TASK
            AuditLog.EntityType.PROJECT.toString() -> AuditLog.EntityType.PROJECT
            else -> AuditLog.EntityType.TASK
        }
    }

    private fun checkAuditLogIsNotValid(auditLog: AuditLogDto): Boolean {
        return (auditLog.id.isEmpty() ||
                auditLog.timestamp <= 0 ||
                auditLog.createdByUserId.isEmpty() ||
                auditLog.entityId.isEmpty()
                )
    }

    private companion object {
        const val NUMBER_OF_ATTRIBUTES = 7
    }
}