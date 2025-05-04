package com.berlin.presentation.task

import com.berlin.domain.exception.InvalidTaskIdException
import com.berlin.domain.exception.TaskNotFoundException
import com.berlin.domain.model.Task
import com.berlin.domain.usecase.task.GetTaskByIdUseCase
import com.berlin.presentation.UiRunner
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer

class GetTaskByIdUI(
    private val getTaskById: GetTaskByIdUseCase,
    private val viewer: Viewer,
    private val reader: Reader,
) : UiRunner {

    override val id: Int = 7
    override val label: String = "Get task by ID"

    override fun run() {
        try {
            viewer.show("Enter task ID:")
            val raw = reader.read()?.trim().orEmpty()
            getTaskById(raw)
                .onSuccess { showTask(it) }
                .onFailure { ex ->
                    when (ex) {
                        is TaskNotFoundException ->
                            viewer.show("No task found with ID “$raw”")
                        else ->
                            viewer.show(ex.message ?: "Lookup failed")
                    }
                }

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
