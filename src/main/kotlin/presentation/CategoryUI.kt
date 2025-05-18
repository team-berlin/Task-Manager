package com.berlin.presentation

import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import data.UserCache

class CategoryUI(
    override val id: Int,
    override val label: String,
    private val children: List<PermissionedUiRunner>,
    private val viewer: Viewer,
    private val reader: Reader,
    private val userCache: UserCache
) : UiRunner {

    private val allowedActions: List<PermissionedUiRunner> by lazy {
        children.filter { it.isAllowed(userCache.currentPermission) }
    }

    override fun run() {
        if (allowedActions.isEmpty()) {
            viewer.show("No available actions in $label.")
            return
        }

        while (true) {
            viewer.show("=== $label ===")
            allowedActions.sortedBy { it.id }.forEach { viewer.show("${it.id} – ${it.label}") }
            viewer.show("X – Back")

            when (val input = reader.read()?.trim()?.uppercase()) {
                null, "X" -> return
                else -> allowedActions.firstOrNull { it.id == input.toIntOrNull() }
                    ?.run() ?: viewer.show("Invalid option: $input. Please select a valid action.")
            }
        }
    }
}