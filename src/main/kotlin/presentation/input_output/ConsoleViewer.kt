package com.berlin.presentation.input_output

class ConsoleViewer : Viewer {
    override fun display(message: String) {
        println(message)
    }
}