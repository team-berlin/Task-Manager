package com.berlin.presentation.audit

import com.berlin.domain.exception.InputCancelledException
import com.berlin.domain.exception.InvalidSelectionException
import com.berlin.domain.model.AuditLog
import com.berlin.domain.model.Permission
import com.berlin.domain.model.Project
import com.berlin.domain.usecase.audit_system.GetAuditLogsByProjectIdUseCase
import com.berlin.domain.usecase.project.GetAllProjectsUseCase
import com.berlin.presentation.PermissionedUiRunner
import com.berlin.presentation.helper.choose
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer

class AuditByProjectUI(
    private val getAuditLogsByProjectIdUseCase: GetAuditLogsByProjectIdUseCase,
    private val getAllProjectsUseCase: GetAllProjectsUseCase,
    private val viewer: Viewer,
    private val reader: Reader
) : PermissionedUiRunner {

    override val id: Int = 1
    override val label: String = "Show audit by project"

    override fun isAllowed(permission: Permission) = permission.getAuditByProject

    override fun run() {
        try {
            val project = selectProject()
            val logs = getAuditLogsByProjectIdUseCase(project.id)

            showProjectLogs(project, logs)

        } catch (ex: InputCancelledException) {
            viewer.show("Cancelled.")
        } catch (ex: InvalidSelectionException) {
            viewer.show("Invalid selection")
        }
    }

    private fun showProjectLogs(project: Project, logs: List<AuditLog>) {
        if (logs.isEmpty()) {
            viewer.show("No audit logs found for project ${project.title}.")
            return
        }
        viewer.show("=== Audit Logs for ${project.title} ===")
        logs.forEach { log ->
            viewer.show(
                """
                    ID: ${log.id}
                    By: ${log.createdByUserId}
                    Action: ${log.auditAction}
                    Changes: ${log.changesDescription ?: "null"}
                """.trimIndent()
            )
            viewer.show("")
        }
    }

    private fun selectProject(): Project {
        return choose(
            title = "Choose a project",
            elements = getAllProjectsUseCase(),
            labelOf = { project -> project.title },
            viewer = viewer,
            reader = reader
        )
    }

}