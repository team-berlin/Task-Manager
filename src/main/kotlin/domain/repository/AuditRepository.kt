package com.berlin.domain.repository

import com.berlin.domain.model.AuditLog

interface AuditRepository {
    fun addAuditLog(auditLog: AuditLog):String
    fun getAuditLogsByProjectId(projectId:String):List<AuditLog>
    fun getAuditLogsByTaskId(taskId:String):List<AuditLog>
    fun getAuditLogsByUserId(userId:String):List<AuditLog>
}