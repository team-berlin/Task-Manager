package com.berlin.presentation.audit

import com.berlin.data.DummyData
import com.berlin.domain.exception.InputCancelledException
import com.berlin.domain.exception.InvalidSelectionException
import com.berlin.domain.model.AuditLog
import com.berlin.domain.model.User
import com.berlin.domain.repository.AuditRepository
import com.berlin.logic.usecase.auditSystem.GetAuditLogsByUserIdUseCase
import com.berlin.presentation.UiRunner
import com.berlin.presentation.helper.choose
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer

class AuditByUserUI(
    private val getAuditLogsByUserIdUseCase: GetAuditLogsByUserIdUseCase,
    private val viewer: Viewer,
    private val reader: Reader
) : UiRunner {

    override val id: Int = 3
    override val label: String = "Show audit by user"

    override fun run() {
        try {
            val selectedUser = selectUser()
            val logs = getAuditLogsByUserIdUseCase.getAuditLogsByUserId(selectedUser.id)

            showUserLogs(selectedUser, logs)

        } catch (ex: InputCancelledException) {
            viewer.show("Cancelled.")
        } catch (ex: InvalidSelectionException) {
            viewer.show("Invalid selection")
        }
    }

    private fun selectUser(): User {
        return choose(
            title = "Choose a user",
            elements = DummyData.users,
            labelOf = { user -> user.userName },
            viewer = viewer,
            reader = reader
        )
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
        }
    }
}
