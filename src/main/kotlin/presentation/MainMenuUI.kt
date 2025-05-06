package com.berlin.presentation

import com.berlin.domain.model.UserRole
import com.berlin.presentation.authService.AuthenticateUserUi
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import data.UserCache

class MainMenuUI(
    private val runners: List<UiRunner>,
    private val viewer: Viewer,
    private val reader: Reader,
    private val authUi: AuthenticateUserUi,
    private val userCache: UserCache
) : UiRunner {

    override val id = 0
    override val label = "Main menu"

    override fun run() {
        viewer.show("===Welcome to our PlanMate===")
        authUi.run()
        val currentUser = userCache.currentUser
        if (currentUser == null) {
            viewer.show("Login failed.")
            return
        }

        viewer.show("===${currentUser.role}===")

        val filteringRunners = when (currentUser.role) {
            UserRole.ADMIN -> runners.filter { it.id in adminPermissionsIds }
            UserRole.MATE -> runners.filter { it.id in matePermissionsIds }

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

    private val adminPermissionsIds = listOf(1, 2, 3, 4, 5, 6, 7, 30, 100, 300, 500, 900)
    private val matePermissionsIds = listOf(1, 2, 3, 4, 5, 6, 7)
}