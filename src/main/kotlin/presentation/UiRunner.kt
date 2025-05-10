package com.berlin.presentation

interface UiRunner {
    val id: Int
    val label: String
    suspend fun run()
}