package com.berlin.logic.repositories

import com.berlin.model.AuditLog

interface AuditRepository {
    fun addAuditLog(auditLog: AuditLog):Result<String>
    fun getAuditLogsByProjectId(projectId:String):List<AuditLog>
    fun getAuditLogsByTaskId(taskId:String):List<AuditLog>
    fun getAuditLogsByUserId(userId:String):List<AuditLog>
}