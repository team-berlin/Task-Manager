package com.berlin.presentation.audit

import com.berlin.domain.exception.InputCancelledException
import com.berlin.domain.exception.InvalidSelectionException
import com.berlin.domain.model.AuditLog
import com.berlin.domain.model.Permission
import com.berlin.domain.model.User
import com.berlin.domain.usecase.audit_system.GetAuditLogsByUserIdUseCase
import com.berlin.domain.usecase.authService.GetAllUsersUseCase
import com.berlin.presentation.PermissionedUiRunner
import com.berlin.presentation.helper.choose
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer

class AuditByUserUI(
    private val getAuditLogsByUserIdUseCase: GetAuditLogsByUserIdUseCase,
    private val fetchAllUsers: GetAllUsersUseCase,
    private val viewer: Viewer,
    private val reader: Reader
) : PermissionedUiRunner {

    override val id: Int = 3
    override val label: String = "Show audit by user"

    override fun isAllowed(permission: Permission) = permission.getAuditByUser

    override fun run() {
        try {
            val selectedUser = selectUser()
            selectedUser.forEach { user ->
                val logs = getAuditLogsByUserIdUseCase.getAuditLogsByUserId(user.id)
                showUserLogs(user, logs)
            }

        } catch (ex: InputCancelledException) {
            viewer.show("Cancelled.")
        } catch (ex: InvalidSelectionException) {
            viewer.show("Invalid selection")
        }
    }

    private fun selectUser(): List<User> {
        val user =  choose(
            title = "Choose a user",
            elements = fetchAllUsers.getAllUsers(),
            labelOf = { user -> user.userName },
            viewer = viewer,
            reader = reader
        )
        return listOf(user)
    }

    private fun showUserLogs(user: User, logs: List<AuditLog>) {
        if (logs.isEmpty()) {
            viewer.show("No audit logs found for user ${user.userName}.")
            return
        }

        viewer.show("=== Audit Logs by ${user.userName} ===")
        logs.sortedBy { it.timestamp }.forEach { log ->
            viewer.show(
                """
                ID: ${log.id}
                Time: ${log.timestamp}
                Action: ${log.auditAction}
                Entity ID: ${log.entityId}
                Changes: ${log.changesDescription ?: "null"}
                """.trimIndent()
            )
            viewer.show("")
        }
    }
}
