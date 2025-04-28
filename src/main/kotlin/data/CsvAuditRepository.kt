package com.berlin.data

import com.berlin.logic.repositories.AuditRepository
import com.berlin.model.AuditLog

class CsvAuditRepository:AuditRepository {
    override fun addAuditLog(auditLog: AuditLog): Boolean {
        return false
    }

    override fun getAuditLogsByProjectId(projectId: Int): List<AuditLog> {
        return emptyList()
    }

    override fun getAuditLogsByTaskId(taskId: Int): List<AuditLog> {
        return emptyList()
    }
}