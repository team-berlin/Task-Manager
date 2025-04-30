package org.berlin.presentation

interface UiRunner {
    val id: Int
    val label: String
    fun run()
}