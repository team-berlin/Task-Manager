package org.berlin.presentation.input_output

class ConsoleViewer : Viewer {
    override fun show(message: String) {
        println(message)
    }
}