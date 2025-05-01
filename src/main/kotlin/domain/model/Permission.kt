package domain.model

data class Permission(
    val createProject: Boolean = false,
    val editProject: Boolean = false,
    val deleteProject: Boolean = false,
    val createState: Boolean = false,
    val editState: Boolean = false,
    val deleteState: Boolean = false,
    val viewTaskById: Boolean = false,
    val viewTasksByProject: Boolean =false,
    val createTask: Boolean = false,
    val editTask: Boolean = false,
    val deleteTask: Boolean = false,
    val assignTask:Boolean = false,
    val viewAuditLogs: Boolean= false
)