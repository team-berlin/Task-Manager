package com.berlin.presentation.io

class ConsoleViewer : Viewer {
    override fun show(message: String) {
        println(message)
    }
}