package com.berlin.data.audit

import com.berlin.data.BaseDataSource
import com.berlin.data.dto.AuditLogDto
import com.berlin.data.mapper.AuditLogMapper
import com.berlin.domain.exception.InvalidAuditLogException
import com.berlin.domain.model.AuditLog
import com.berlin.domain.repository.AuditRepository

class AuditRepositoryImpl(
    private val auditLogDataSource: BaseDataSource<AuditLogDto>,
    private val auditLogMapper: AuditLogMapper
) : AuditRepository {

    override fun addAuditLog(auditLog: AuditLog): String {
        val auditLogDto = auditLogMapper.mapToDataModel(auditLog)
         if (auditLogDataSource.write(auditLogDto)) {
            return auditLog.id
        } else {
            throw InvalidAuditLogException("fail to add audit log")
        }
    }

    override fun getAuditLogsByProjectId(projectId: String): List<AuditLog> {
        val allAuditLogs = auditLogDataSource.getAll().map {
            auditLogMapper.mapToDomainModel(it)
        }
        return allAuditLogs.filter { it.entityType.name == "PROJECT" && it.entityId == projectId }
    }

    override fun getAuditLogsByTaskId(taskId: String): List<AuditLog> {
        val allAuditLogs = auditLogDataSource.getAll().map {
            auditLogMapper.mapToDomainModel(it)
        }
        return allAuditLogs.filter { it.entityType.name == "TASK" && it.entityId == taskId }
    }

    override fun getAuditLogsByUserId(userId: String): List<AuditLog> {
        val allAuditLogs = auditLogDataSource.getAll().map {
            auditLogMapper.mapToDomainModel(it)
        }
        return allAuditLogs.filter { it.createdByUserId == userId }
    }
}