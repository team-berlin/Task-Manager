package com.berlin

import com.google.common.truth.Truth.assertThat
import io.mockk.*
import org.junit.jupiter.api.Test
import org.koin.core.context.stopKoin
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class MainTest {

    @Test
    fun `main prints banner`() {
        mockkStatic("kotlin.io.ConsoleKt")
        every { readLine() } returns "X"

        val originalOut = System.out
        val buffer      = ByteArrayOutputStream()
        System.setOut(PrintStream(buffer))

        main()

        val output = buffer.toString()
        assertThat(output).contains("=== Task Manager")

        verify(exactly = 1) { readLine() }

        System.setOut(originalOut)
    }
}
