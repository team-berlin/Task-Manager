package com.berlin.presentation

import com.berlin.domain.model.UserRole
import com.berlin.presentation.authService.AuthenticateUserUI
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import data.UserCache

class MainMenuUI(
    private val runners: List<UiRunner>,
    private val viewer: Viewer,
    private val reader: Reader,
    private val authUi: AuthenticateUserUI,
    private val userCache: UserCache,
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

        viewer.show("===${currentUser.role} Board===")

        val filteringRunners = filterRunners(currentUser.role)

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

    private fun filterRunners(userRole: UserRole): List<UiRunner> =
        when (userRole) {
            UserRole.ADMIN -> runners.filter { it.id in adminPermissionFilterIds }
            UserRole.MATE -> runners.filter { it.id in matePermissionFilterIds }
        }

    private fun showMenu(runners: List<UiRunner>) {
        runners.sortedBy { it.id }
            .forEach { viewer.show("${it.id} – ${it.label}") }
        viewer.show("X – Exit")
        viewer.show("Select an option:")
    }


    private companion object {
        val adminPermissionFilterIds = listOf(1, 2, 3, 4, 5, 6, 7, 30,50, 100, 300, 500, 900, 11, 12, 13, 14, 15, 24390823,1000,2000,3000,200000, 2349, 24234)
        val matePermissionFilterIds = listOf(1, 2, 3, 4, 5, 6, 7)
    }
}