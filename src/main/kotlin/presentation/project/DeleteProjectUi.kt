package com.berlin.presentation.project

import com.berlin.domain.exception.InputCancelledException
import com.berlin.domain.exception.InvalidProjectException
import com.berlin.domain.exception.InvalidProjectIdException
import com.berlin.domain.exception.InvalidSelectionException
import com.berlin.domain.model.Permission
import com.berlin.domain.usecase.project.DeleteProjectUseCase
import com.berlin.domain.usecase.project.GetAllProjectsUseCase
import com.berlin.presentation.PermissionedUiRunner
import com.berlin.presentation.helper.choose
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer

class DeleteProjectUi(
    private val deleteProject: DeleteProjectUseCase,
    private val getAllProjects: GetAllProjectsUseCase,
    private val viewer: Viewer,
    private val reader: Reader
) : PermissionedUiRunner {
    override val id: Int = 2
    override val label: String = "Delete Project"

    override fun isAllowed(permission: Permission) = permission.deleteProject

    override fun run() {
        try {
            val project = choose(
                title = "Projects",
                elements = getAllProjects(),
                labelOf = { "${it.id} – ${it.title}" },
                viewer = viewer,
                reader = reader
            )

            viewer.show("Type Y to confirm deletion:")
            if (!reader.read().equals("y", true)) throw InputCancelledException("")

            val projectName = deleteProject(project.id)
                    viewer.show("${project.title} is Deleted.")

        } catch (ex: InputCancelledException) {
            viewer.show("Cancelled.")
        } catch (ex: InvalidSelectionException) {
            viewer.show("Invalid selection")
        } catch (ex: InvalidProjectIdException) {
            viewer.show("invalid project id")
        }
        catch (_: InvalidProjectException){
            viewer.show("delete is failed")
        }
    }
}