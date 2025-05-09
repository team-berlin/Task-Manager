package com.berlin.presentation


import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer

class ManageusersMainUi(
    private val usersRunners: List<UiRunner>,
    private val viewer : Viewer,
    private val reader : Reader
) : UiRunner
{
    override val id: Int=2
    override val label: String="Manage Users"
    override fun run(){
        showMenu()
        when (val input = reader.read()?.trim()) {
            null, "", "X", "x" -> return
            else -> usersRunners
                .firstOrNull { it.id == input.toIntOrNull() }
                ?.run()
                ?: viewer.show("Invalid choice: $input")
        }
    }

    private fun showMenu() {
        viewer.show("=== Manage Users ===")
        usersRunners.sortedBy { it.id }
            .forEach { viewer.show("${it.id} – ${it.label}") }
        viewer.show("X – Exit")
        viewer.show("Select an option:")
    }

}
