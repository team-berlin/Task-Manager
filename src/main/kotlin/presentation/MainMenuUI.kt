//package org.berlin.presentation
//
//import org.berlin.presentation.input_output.Reader
//import org.berlin.presentation.input_output.Viewer
//
//class MainMenuUI(
//    private val runners: List<UiRunner>,
//    private val viewer: Viewer,
//    private val reader: Reader
//) : UiRunner {
//    override val id = 0
//    override val label = "Main menu"
//
//    override fun run() {
//        while (true) {
//            showMenu()
//            when (val input = reader.getUserInput()?.trim()) {
//                null, "", "X", "x" -> return
//                else -> {
//                    input.toIntOrNull()?.let { choice ->
//                        runners.find { it.id == choice }
//                    }?.run() ?: viewer.display("Invalid choice: $input")
//                }
//            }
//        }
//    }
//
//    private fun showMenu() {
//        viewer.display("=== Food Change Mood ===")
//        runners.sortedBy { it.id }.forEach { ui ->
//                viewer.display("${ui.id} - ${ui.label}")
//            }
//        viewer.display("X - Exit")
//        viewer.display("Select an option:")
//    }
//}

package org.berlin.presentation

import org.berlin.presentation.input_output.Reader
import org.berlin.presentation.input_output.Viewer

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
                null, "", "X", "x" -> return            // ← leave main menu
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
}
