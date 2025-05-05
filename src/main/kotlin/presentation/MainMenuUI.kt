package com.berlin.presentation

import com.berlin.presentation.authService.AuthenticateUserUi
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import data.UserCache

class MainMenuUI(
    private val runners: List<UiRunner>,
    private val viewer : Viewer,
    private val reader : Reader,
    private val authUi: AuthenticateUserUi
) : UiRunner {

    override val id = 0
    override val label = "Main menu"

    override fun run() {
        authUi.run()
        val currentUser = UserCache.currentUser
        if (currentUser == null) {
            viewer.show("Login failed.")
            return
        }

        viewer.show("===${currentUser.role}===")

        val filteringRunners = when (currentUser.role.name.uppercase()) {
            "ADMIN" -> runners.filter { it.id in adminIds }
            "MATE" -> runners.filter { it.id in mateIds }
            else -> {
                viewer.show("Unknown role: ${currentUser.role}")
                return
            }
        }

        while (true) {
            showMenu(filteringRunners)
            when (val input = reader.read()?.trim()) {
                null, "", "x", "X" -> return
                else -> filteringRunners
                    .firstOrNull { it.id == input.toIntOrNull() }
                    ?.run()
                    ?: viewer.show("Invalid choice: $input")
            }
        }
    }

    private fun showMenu(runners: List<UiRunner>) {
        viewer.show("=== Task Manager ===")
        runners.sortedBy { it.id }
            .forEach { viewer.show("${it.id} – ${it.label}") }
        viewer.show("X – Exit")
        viewer.show("Select an option:")
    }

    private val adminIds = listOf(1, 2, 3, 4, 5, 6, 7, 30, 100, 300, 500, 900)
    private val mateIds = listOf(1, 2, 3, 4, 5, 6, 7)
}