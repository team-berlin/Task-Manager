package com.berlin.presentation.task

import com.berlin.domain.exception.InvalidTaskIdException
import com.berlin.domain.exception.TaskNotFoundException
import com.berlin.domain.model.Permission
import com.berlin.domain.model.Task
import com.berlin.domain.usecase.task.GetTaskByIdUseCase
import com.berlin.presentation.PermissionedUiRunner
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer

class GetTaskByIdUI(
    private val getTaskById: GetTaskByIdUseCase,
    private val viewer: Viewer,
    private val reader: Reader,
) : PermissionedUiRunner {

    override val id: Int = 7
    override val label: String = "Get task by ID"

    override fun isAllowed(permission: Permission) = permission.getTaskById

    override fun run() {
        try {
            viewer.show("Enter task ID:")
            val raw = reader.read()?.trim().orEmpty()
            val task = getTaskById(raw)
            showTask(task)
        } catch (ex: InvalidTaskIdException) {
            viewer.show("Invalid task id")
        }
    }

    private fun showTask(t: Task) {
        viewer.show("ID: ${t.id}")
        viewer.show("Title: ${t.title}")
        viewer.show("Description: ${t.description ?: "(none)"}")
        viewer.show("State: ${t.stateId}")
        viewer.show("Assignee: ${t.assignedToUserId}")
        viewer.show("Created by: ${t.createByUserId}")
    }
}
