package org.berlin.presentation.input_output

class ConsoleReader: Reader {
    override fun read(): String? {
        return readlnOrNull()
    }
}