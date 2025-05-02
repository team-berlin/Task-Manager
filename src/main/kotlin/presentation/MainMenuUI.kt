package com.berlin.presentation

import com.berlin.domain.model.UserRole
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import data.UserCache

class MainMenuUI(
    private val logInUI: List<UiRunner>,
    private val viewer: Viewer,
    private val reader: Reader,
    private val adminRunners: List<UiRunner>,
    private val mateRunners: List<UiRunner>,
) : UiRunner {

    override val id = 0
    override val label = "Main menu"
    private lateinit var runners: List<UiRunner>
    override fun run() {
        viewer.show("=== Welcome to our PlanMate app ===")
        logInUI.forEach { it.run() }
        runners = giveUserPermission(UserCache) ?: return

        val title = when (UserCache.currentUser?.role) {
            UserRole.ADMIN -> "Admin"
            UserRole.MATE -> "Mate"
            else -> "User"
        }

        while (true) {
            showMenu(title)
            when (val input = reader.read()?.trim()) {
                null, "", "X", "x" -> return
                else -> runners
                    .firstOrNull { it.id == input.toIntOrNull() }
                    ?.run()
                    ?: viewer.show("Invalid choice: $input")
            }
        }
    }

    private fun showMenu(title: String) {
        viewer.show("=== $title Menu ===")
        runners.sortedBy { it.id }
            .forEach { viewer.show("${it.id} – ${it.label}") }
        viewer.show("X – Exit")
        viewer.show("Select an option:")
    }

    private fun giveUserPermission(currentUser: UserCache): List<UiRunner>? {
        return when (currentUser.currentUser?.role) {
            UserRole.ADMIN -> adminRunners
            UserRole.MATE -> mateRunners
            else -> null
        }
    }
}
