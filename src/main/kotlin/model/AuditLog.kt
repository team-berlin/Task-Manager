package com.berlin.model


data class AuditLog(
    val id:String,
    val timestamp: Long,
    val createdBy:User,
    val auditAction:AuditAction,
    val changesDescription:String,
    val entityType:EntityType,
    val entityId:String
)
