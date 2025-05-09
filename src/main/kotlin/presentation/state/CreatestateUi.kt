package com.berlin.presentation.state

import com.berlin.data.DummyData
import com.berlin.domain.exception.InputCancelledException
import com.berlin.domain.model.Project
import com.berlin.domain.usecase.state.CreateStateUseCase
import com.berlin.presentation.UiRunner
import com.berlin.presentation.helper.choose
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer

class CreateStateUi(
    private val createStateUseCase: CreateStateUseCase,
    private val viewer: Viewer,
    private val reader: Reader
) : UiRunner {

    override val id: Int = 1000
    override val label: String = "Create New State"

    override fun run() {

        try {
            val project = selectProject()
            viewer.show("-- Enter a state in project ${project.name} --")
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
        title = "Projects", elements = DummyData.projects, labelOf = { it.name }, viewer = viewer, reader = reader
    )

}