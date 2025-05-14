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

): UiRunner {

    override fun run() {
        val permission = userCache.currentPermission
        val allowed = children.filter { it.isAllowed(permission) }

        if (allowed.isEmpty()) {
            viewer.show("No available actions in $label.")
            return
        }

        while (true) {
            viewer.show("=== $label ===")
            allowed.sortedBy { it.id }
                .forEach { viewer.show("${it.id} – ${it.label}") }
            viewer.show("X – Back")

            when (val input = reader.read()?.trim()?.uppercase(Locale.getDefault())) {
                null,"X" -> return
                else -> allowed
                    .firstOrNull { it.id == input.toIntOrNull() }
                    ?.run()
                    ?: viewer.show("Invalid choice")
            }
        }
    }
}