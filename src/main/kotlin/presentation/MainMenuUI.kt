package com.berlin.presentation

import com.berlin.domain.permission.assignPermissions
import com.berlin.presentation.authService.AuthenticateUserUI
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import data.UserCache
import java.util.*

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
        userCache.currentPermission = assignPermissions(currentUser.role)

        viewer.show("===${currentUser.role} Board===")

        while (true) {
            showMenu(runners)
            when (val input = reader.read()?.trim()?.uppercase(Locale.getDefault())) {
                null,"X" -> return
                else -> runners.firstOrNull { it.id == input.toIntOrNull() }?.run()
                    ?: viewer.show("Invalid choice")
            }
        }
    }

    private fun showMenu(runners: List<UiRunner>) {
        runners.sortedBy { it.id }.forEach { viewer.show("${it.id} – ${it.label}") }
        viewer.show("X – Exit")
        viewer.show("Select an option:")
    }

}