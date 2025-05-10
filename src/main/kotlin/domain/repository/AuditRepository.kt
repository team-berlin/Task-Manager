package com.berlin.domain.repository

import com.berlin.domain.model.AuditLog

interface AuditRepository {
    suspend fun addAuditLog(auditLog: AuditLog):Result<String>
    suspend fun getAuditLogsByProjectId(projectId:String):List<AuditLog>
    suspend fun getAuditLogsByTaskId(taskId:String):List<AuditLog>
    suspend fun getAuditLogsByUserId(userId:String):List<AuditLog>
}