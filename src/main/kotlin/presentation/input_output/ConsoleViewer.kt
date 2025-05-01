package com.berlin.presentation.input_output

import presentation.input_output.Viewer

class ConsoleViewer: Viewer {
    override fun display(message: String) {
        println(message)
    }
}