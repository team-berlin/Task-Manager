package com.berlin.data.schema

import com.berlin.data.AuditLogIndex
import com.berlin.data.BaseSchema
import com.berlin.domain.model.AuditAction
import com.berlin.domain.model.AuditLog
import com.berlin.domain.model.EntityType

class AuditSchema(
    override val fileName: String, override val header: List<String>
) : BaseSchema<AuditLog> {

    init {
        require(
            fileName.isNotEmpty() && header.size == NUMBER_OF_ATTRIBUTES
        )
    }

    override fun toRow(entity: AuditLog): List<String> {
        return if (checkAuditLogIsNotValid(entity)) emptyList()
        else auditLogToStringsList(entity)
    }

    override fun fromRow(row: List<String>): AuditLog? {
        return if (checkRowIsNotValidAuditLog(row)) null
        else stringsListToAuditLog(row)
    }

    override fun getId(entity: AuditLog): String? {
        return entity.id.ifEmpty { null }
    }

    private fun auditLogToStringsList(auditLog: AuditLog): List<String> {
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

    private fun stringsListToAuditLog(row: List<String>): AuditLog {
        return AuditLog(
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
                row[AuditLogIndex.AUDIT_ACTION] !in enumValues<AuditAction>().map { it.name } ||
                row[AuditLogIndex.ENTITY_TYPE] !in enumValues<EntityType>().map { it.name }
                )
    }

    private fun stringToAuditAction(auditAction: String): AuditAction {
        return when (auditAction) {
            AuditAction.CREATE.toString() -> AuditAction.CREATE
            AuditAction.UPDATE.toString() -> AuditAction.UPDATE
            AuditAction.DELETE.toString() -> AuditAction.DELETE
            else -> AuditAction.CREATE
        }
    }

    private fun stringToEntityType(entityType: String): EntityType {
        return when (entityType) {
            EntityType.TASK.toString() -> EntityType.TASK
            EntityType.PROJECT.toString() -> EntityType.PROJECT
            else -> EntityType.TASK
        }
    }

    private fun checkAuditLogIsNotValid(auditLog: AuditLog): Boolean {
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