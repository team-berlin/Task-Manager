package com.berlin.presentation.audit

import com.berlin.data.DummyData
import com.berlin.domain.exception.InputCancelledException
import com.berlin.domain.exception.InvalidSelectionException
import com.berlin.domain.model.AuditLog
import com.berlin.domain.model.Project
import com.berlin.domain.model.Task
import com.berlin.domain.usecase.auditSystem.GetAuditLogsByTaskIdUseCase
import com.berlin.domain.usecase.project.GetAllProjectsUseCase
import com.berlin.domain.usecase.task.GetTasksByProjectUseCase
import com.berlin.presentation.UiRunner
import com.berlin.presentation.helper.choose
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer

class AuditByTaskUI(
    private val viewer: Viewer,
    private val reader: Reader,
    private val getAuditLogsByTaskIdUseCase: GetAuditLogsByTaskIdUseCase,
    private val getTasksByProjectUseCase: GetTasksByProjectUseCase,
    private val getAllProjectsUseCase: GetAllProjectsUseCase

) : UiRunner {

    override val id = 2349
    override val label = "View Audit Logs by Task"

    override fun run() {
        try {
            val selectedProject = selectProject()

            val selectedTask = selectTask(selectedProject)

            val logs = getAuditLogsByTaskIdUseCase.getAuditLogsByTaskId(selectedTask.id)
            showAuditLogs(logs)
        } catch (e: InputCancelledException) {
            viewer.show("Cancelled.")
        } catch (e: InvalidSelectionException) {
            viewer.show("Invalid selection")
        }


    }

    private fun showAuditLogs(logs: List<AuditLog>) {
        if (logs.isEmpty()) {
            viewer.show("No audit logs found for this task.")
            return
        }

        viewer.show("=== Audit Logs for Task ===")
        logs.sortedBy { it.timestamp }.forEach { log ->
            viewer.show(
                """
                ID: ${log.id}
                Time: ${log.timestamp}
                By: ${log.createdByUserId}
                Action: ${log.auditAction}
                Entity ID: ${log.entityId}
                Changes: ${log.changesDescription ?: "null"}
            """.trimIndent()
            )
            viewer.show("")
        }
    }

    private fun selectProject () : Project{
        return choose(
            title = "Choose a project",
            elements = getAllProjectsUseCase.getAllProjects(),
            labelOf = { project -> project.name },
            viewer = viewer,
            reader = reader
        )
    }

    private fun selectTask (selectedProject : Project) : Task {
        return choose(
            title = "Choose a task",
            elements = getTasksByProjectUseCase( selectedProject.id).getOrNull() ?: emptyList() ,
            labelOf = { task -> task.title },
            viewer = viewer,
            reader = reader
        )
    }
}

