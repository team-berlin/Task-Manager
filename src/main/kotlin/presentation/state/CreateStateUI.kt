package com.berlin.presentation.state

import com.berlin.domain.exception.InputCancelledException
import com.berlin.domain.model.Permission
import com.berlin.domain.model.Project
import com.berlin.domain.usecase.project.GetAllProjectsUseCase
import com.berlin.domain.usecase.state.CreateStateUseCase
import com.berlin.presentation.PermissionedUiRunner
import com.berlin.presentation.helper.choose
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer

class CreateStateUI(
    private val createStateUseCase: CreateStateUseCase,
    private val getAllProjectUseCase: GetAllProjectsUseCase,
    private val viewer: Viewer,
    private val reader: Reader
) : PermissionedUiRunner {

    override val id: Int = 1
    override val label: String = "Create New State"

    override fun isAllowed(permission: Permission) = permission.createState

    override fun run() {

        try {
            val project = selectProject()
            viewer.show("-- Enter a state in project ${project.title} --")
            addStateName(project)
        } catch (_: InputCancelledException) {
            viewer.show("Cancelled!")
        }


    }

    private fun addStateName(project: Project) {

        viewer.show("Enter a state name (or type 'exit' to finish):")
        viewer.show("State Name: ")
        val stateName: String? = reader.read()?.trim()
        when {

            (stateName?.lowercase().equals("exit")) -> return

            (stateName.isNullOrEmpty()) -> {
                viewer.show("State Name can not be empty")
            }

            else -> {
                try {
                    createStateUseCase.createNewState(stateName, project.id)
                        .onSuccess { viewer.show(it) }
                        .onFailure { viewer.show(it.message ?: "Creation failed") }

                } catch (_: Exception) {
                    viewer.show("Invalid State Name, Try Again")
                }
            }
        }
        addStateName(project)
    }

    private fun selectProject() = choose(
        title = "Projects", elements = getAllProjectUseCase.getAllProjects(), labelOf = { it.title }, viewer = viewer, reader = reader
    )

}