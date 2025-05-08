package com.berlin.presentation.audit

import com.berlin.data.DummyData
import com.berlin.domain.exception.InputCancelledException
import com.berlin.domain.exception.InvalidSelectionException
import com.berlin.domain.model.AuditLog
import com.berlin.domain.model.Project
import com.berlin.domain.usecase.auditSystem.GetAuditLogsByProjectIdUseCase
import com.berlin.domain.usecase.project.GetAllProjectsUseCase
import com.berlin.domain.usecase.task.GetTasksByProjectUseCase
import com.berlin.presentation.UiRunner
import com.berlin.presentation.helper.choose
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer

class AuditByProjectUI(
    private val getAuditLogsByProjectIdUseCase: GetAuditLogsByProjectIdUseCase,
    private val getAllProjectsUseCase: GetAllProjectsUseCase,
    private val viewer: Viewer,
    private val reader: Reader
) : UiRunner {

    override val id: Int = 24390823
    override val label: String = "Show audit by project"

    override fun run() {
        try {
            val project = selectProject()
            val logs = getAuditLogsByProjectIdUseCase.getAuditLogsByProjectId(project.id)

            showProjectLogs(project, logs)

        } catch (ex: InputCancelledException) {
            viewer.show("Cancelled.")
        } catch (ex: InvalidSelectionException) {
            viewer.show("Invalid selection")
        }
    }

    private fun showProjectLogs(project: Project, logs: List<AuditLog>) {
        if (logs.isEmpty()) {
            viewer.show("No audit logs found for project ${project.name}.")
            return
        }
        viewer.show("=== Audit Logs for ${project.name} ===")
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
            elements = getAllProjectsUseCase.getAllProjects(),
            labelOf = { project -> project.name },
            viewer = viewer,
            reader = reader
        )
    }

}