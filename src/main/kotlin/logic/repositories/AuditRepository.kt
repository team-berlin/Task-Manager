package com.berlin.logic.repositories

import com.berlin.model.AuditLog

interface AuditRepository {
    fun addAuditLog(auditLog: AuditLog):Boolean
    fun getAuditLogsByProjectId(projectId:Int):List<AuditLog>
    fun getAuditLogsByTaskId(taskId:Int):List<AuditLog>

}