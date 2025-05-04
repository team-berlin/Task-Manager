package com.berlin.presentation

import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer

class MainMenuUI(
    private val runners: List<UiRunner>,
    private val viewer : Viewer,
    private val reader : Reader
) : UiRunner {

    override val id    = 0
    override val label = "Main menu"

    override fun run() {
        while (true) {
            showMenu()
            when (val input = reader.read()?.trim()) {
                null, "", "X", "x" -> return
                else -> runners
                    .firstOrNull { it.id == input.toIntOrNull() }
                    ?.run()
                    ?: viewer.show("Invalid choice: $input")
            }
        }
    }

    private fun showMenu() {
        viewer.show("=== Task Manager ===")
        runners.sortedBy { it.id }
            .forEach { viewer.show("${it.id} – ${it.label}") }
        viewer.show("X – Exit")
        viewer.show("Select an option:")
    }
    private fun showOptions(){
        viewer.show("")

    }
}