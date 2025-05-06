package com.berlin.model

import com.berlin.domain.model.AuditLog
import com.berlin.domain.model.User


data class Task(
    val id:String,
    val projectId:String,
    val title:String,
    val description:String?,
    val stateId:String,
    val assignedTo: User,
    val createBy:User,
    val auditLogs:List<AuditLog>
    )
