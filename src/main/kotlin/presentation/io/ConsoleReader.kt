package com.berlin.presentation.io

class ConsoleReader: Reader {
    override fun read(): String? {
        return readlnOrNull()
    }
}