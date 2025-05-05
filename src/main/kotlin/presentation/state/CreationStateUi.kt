package com.berlin.presentation.state

import com.berlin.data.DummyData
import com.berlin.domain.model.Project
import com.berlin.domain.usecase.state.CreationStateUseCase
import com.berlin.presentation.UiRunner
import com.berlin.presentation.helper.choose
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer

class CreationStateUi(
    private val creationStateUseCase: CreationStateUseCase,
    private val viewer: Viewer,
    private val reader: Reader
) : UiRunner {

    override val id: Int = 1
    override val label: String = "Create New State"

    override fun run() {

        val project = selectProject()
        addStateName(project)

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
                    creationStateUseCase.createNewState(stateName, project.id)
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