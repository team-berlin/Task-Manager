package com.berlin

import com.google.common.truth.Truth.assertThat
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.koin.core.context.stopKoin
import java.io.ByteArrayOutputStream
import java.io.PrintStream
class MainTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `main prints banner`() = runTest {
        mockkStatic("kotlin.io.ConsoleKt")
        coEvery { readLine() } returns "X"

        val originalOut = System.out
        val buffer = ByteArrayOutputStream()
        System.setOut(PrintStream(buffer))

        main()

        val output = buffer.toString()
        assertThat(output).contains("=== Task Manager")

        verify(exactly = 1) { readLine() }

        System.setOut(originalOut)
    }
}
