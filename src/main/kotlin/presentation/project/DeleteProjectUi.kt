package com.berlin.presentation.project

import com.berlin.data.DummyData
import com.berlin.domain.exception.InputCancelledException
import com.berlin.domain.exception.InvalidProjectIdException
import com.berlin.domain.exception.InvalidSelectionException
import com.berlin.domain.usecase.project.DeleteProjectUseCase
import com.berlin.domain.usecase.project.GetAllProjectsUseCase
import com.berlin.presentation.UiRunner
import com.berlin.presentation.helper.choose
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer

class DeleteProjectUi(
    private val deleteProject: DeleteProjectUseCase,
    private val getAllProjects: GetAllProjectsUseCase,
    private val viewer: Viewer,
    private val reader: Reader
) : UiRunner {
    override val id: Int = 2
    override val label: String = "Delete Project"


    override fun run() {
        try {
            val project = choose(
                title = "Projects",
                elements = getAllProjects.getAllProjects(),
                labelOf = { "${it.id} – ${it.name}" },
                viewer = viewer,
                reader = reader
            )

            viewer.show("Type Y to confirm deletion:")
            if (!reader.read().equals("y", true)) throw InputCancelledException("")

            deleteProject.deleteProject(project.id)
                .onSuccess {
                    DummyData.projects.remove(project)
                    viewer.show("Deleted.")
                }
                .onFailure {
                    viewer.show(it.message ?: "Deletion failed")
                }

        } catch (ex: InputCancelledException) {
            viewer.show("Cancelled.")
        } catch (ex: InvalidSelectionException) {
            viewer.show("Invalid selection")
        } catch (ex: InvalidProjectIdException) {
            viewer.show("invalid project id")
        }
    }
}