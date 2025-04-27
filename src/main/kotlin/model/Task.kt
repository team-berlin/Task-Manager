package com.berlin.model


data class Task(
    val id:Int,
    val title:String,
    val description:String?,
    val stateId:Int,
    val assignedTo:User,
    val createBy:User,
    val auditLogs:List<AuditLog>
    )
