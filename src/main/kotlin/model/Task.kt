package com.berlin.model


data class Task(
    val id:String,
    val projectId:String,
    val title:String,
    val description:String?,
    val stateId:String,
    val assignedTo:User,
    val createBy:User,
    val auditLogs:List<AuditLog>
    )
