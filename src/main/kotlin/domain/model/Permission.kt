package com.berlin.domain.model

import org.bson.codecs.pojo.annotations.BsonProperty

data class Permission(
    @BsonProperty("create_project") val createProject: Boolean = false,
    @BsonProperty("edit_project") val editProject: Boolean = false,
    @BsonProperty("delete_project") val deleteProject: Boolean = false,
    @BsonProperty("create_state") val createState: Boolean = false,
    @BsonProperty("edit_state") val editState: Boolean = false,
    @BsonProperty("delete_state") val deleteState: Boolean = false,
    @BsonProperty("view_task_by_id") val viewTaskById: Boolean = false,
    @BsonProperty("view_tasks_by_project") val viewTasksByProject: Boolean = false,
    @BsonProperty("create_task") val createTask: Boolean = false,
    @BsonProperty("edit_task") val editTask: Boolean = false,
    @BsonProperty("delete_task") val deleteTask: Boolean = false,
    @BsonProperty("assign_task") val assignTask: Boolean = false,
    @BsonProperty("view_audit_logs") val viewAuditLogs: Boolean = false
)