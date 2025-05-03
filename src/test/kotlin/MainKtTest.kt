package com.berlin

import com.google.common.truth.Truth.assertThat
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.koin.core.context.stopKoin
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class MainTest {

    @Test
    fun `main prints banner`() {
        /* ── 1  Stub readLine() so the CLI exits right away ───────── */
        mockkStatic("kotlin.io.ConsoleKt")
        every { readlnOrNull() } returns "X"

        /* ── 2  Capture System.out ─────────────────────────────────── */
        val originalOut = System.out
        val buffer      = ByteArrayOutputStream()
        System.setOut(PrintStream(buffer))

        /* ── 3  Run the real entry-point (= gives coverage) ────────── */
        main()                              // com.berlin.MainKt.main()

        /* ── 4  Assertions ─────────────────────────────────────────── */
        val output = buffer.toString()
        assertThat(output).contains("=== Task Manager ===")

        /* verify readLine() was actually called */
        verify(exactly = 1) { readlnOrNull() }

        /* restore stdout for other tests */
        System.setOut(originalOut)
    }
}
