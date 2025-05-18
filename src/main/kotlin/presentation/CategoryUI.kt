package com.berlin.presentation

import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import data.UserCache
import java.util.*

class CategoryUI(
    override val id: Int,
    override val label: String,
    private val children: List<PermissionedUiRunner>,
    private val viewer: Viewer,
    private val reader: Reader,
    private val userCache: UserCache
) : UiRunner {

    private val allowed by lazy { children.filter { it.isAllowed(userCache.currentPermission) } }

    override fun run() {
        if (allowed.isEmpty()) {
            viewer.show("No available actions in $label.")
            return
        }

        executeMenu()
    }

    private fun executeMenu() {
        while (true) {
            showMenu()
            val input = reader.read()?.trim()?.uppercase(Locale.getDefault())

            if (input.isNullOrEmpty() || input == "X") return

            allowed.firstOrNull { it.id == input.toIntOrNull() }
                ?.run()
                ?: viewer.show("Invalid choice")
        }
    }

    private fun showMenu() {
        viewer.show("=== $label ===")
        allowed.sortedBy { it.id }
            .forEach { viewer.show("${it.id} – ${it.label}") }
        viewer.show("X – Back")
    }
}
