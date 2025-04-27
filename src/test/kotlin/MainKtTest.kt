package com.berlin

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class MainTest {

    @Test
    fun `main prints Hello World`() {
        val buffer = ByteArrayOutputStream()
        System.setOut(PrintStream(buffer))

        main()

        val output = buffer.toString().trim()
        assertThat(output)
            .isEqualTo("Hello World!")
    }
}
