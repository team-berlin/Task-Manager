package com.berlin.presentation


import com.berlin.domain.model.UserRole
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer

class ManageProjcetsMainUi(
    private val projectRunners: List<UiRunner>,
    private val viewer: Viewer,
    private val reader: Reader,
    private val userRole: UserRole,
) : UiRunner {
    override val id: Int = 1
    override val label: String = "Continue to Manage Your Projects"
    override fun run() {
        val allowedRunners=filterOptionsBasedOnRole()

        showMenu(allowedRunners)

        when (val input = reader.read()?.trim()) {
            null, "", "X", "x" -> return
            else -> allowedRunners
                .firstOrNull { it.id == input.toIntOrNull() }
                ?.run()
                ?: viewer.show("Invalid choice: $input")
        }
    }

    private fun showMenu(allowedRunners: List<UiRunner>) {

        viewer.show("=== Manage your projects ===")
        allowedRunners.sortedBy { it.id }
            .forEach { viewer.show("${it.id} – ${it.label}") }
        viewer.show("X – Exit")
        viewer.show("Select an option:")
    }

    private fun filterOptionsBasedOnRole() = projectRunners.filter {
        it !is RoleBasedPermission || userRole in it.allowedRoles

    }
}
