package com.berlin.data.audit

import com.berlin.data.BaseDataSource
import com.berlin.domain.exception.InvalidAuditLogException
import com.berlin.domain.model.AuditLog
import com.berlin.domain.repository.AuditRepository

class AuditRepositoryImpl (private val auditLogDataSource: BaseDataSource<AuditLog>) : AuditRepository {

    override fun addAuditLog(auditLog: AuditLog): Result<String> {
        return if (auditLogDataSource.write(auditLog)) {
            Result.success(auditLog.id)
        } else {
            Result.failure(InvalidAuditLogException("fail to add audit log"))
        }
    }

    override fun getAuditLogsByProjectId(projectId: String): List<AuditLog> {
        return auditLogDataSource.getAll().filter { it.entityType.name == "PROJECT" && it.entityId == projectId }
    }

    override fun getAuditLogsByTaskId(taskId: String): List<AuditLog> {
        return auditLogDataSource.getAll().filter { it.entityType.name == "TASK" && it.entityId == taskId }
    }

    override fun getAuditLogsByUserId(userId: String): List<AuditLog> {
        return auditLogDataSource.getAll().filter { it.createdByUserId == userId }
    }
}