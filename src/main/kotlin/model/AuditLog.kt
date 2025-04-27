package com.berlin.model

import java.sql.Timestamp

data class AuditLog(
    val id:Int,
    val timestamp: Timestamp,
    val createdBy:User,
    val auditAction:AuditAction,
    val changesDescription:String,
    val auditForType:AuditSearchType,
    val idForAuditType:Int
)
