package presentation

import com.berlin.presentation.MainMenuUI
import com.berlin.presentation.UiRunner
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import com.google.common.truth.Truth.assertThat
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class MainMenuUITest {

    private lateinit var viewer: Viewer
    private lateinit var reader: Reader
    private lateinit var menu: MainMenuUI

    private val printed = mutableListOf<String>()

    @BeforeEach
    fun setUp() {
        viewer = mockk(relaxed = true) {
            coEvery { show(capture(printed)) } just Runs
        }
        reader = mockk()
    }

    @Test
    fun `exit immediately on blank input without running any runner`() = runTest {
        coEvery { reader.read() } returns ""
        menu = MainMenuUI(emptyList(), viewer, reader)

        menu.run()

        // should have printed the menu once, then returned
        assert(printed.first().contains("=== Task Manager ==="))
    }

    @Test
    fun `runs matching runner then exits on X`() = runTest {
        val r0 = object : UiRunner {
            override val id = 0
            override val label = "zero"
            var invoked = 0
            override suspend fun run() {
                invoked++
            }
        }
        val r1 = object : UiRunner {
            override val id = 1
            override val label = "one"
            var invoked = 0
            override suspend fun run() {
                invoked++
            }
        }

        coEvery { reader.read() } returnsMany listOf("1", "X")
        menu = MainMenuUI(listOf(r0, r1), viewer, reader)

        menu.run()

        assert(r1.invoked == 1)
        assert(r0.invoked == 0)

        val banners = printed.filter { it.contains("=== Task Manager ===") }
        assert(banners.size == 2)
    }

    @Test
    fun `invalid choice prints error then exits`() = runTest {
        val dummy = object : UiRunner {
            override val id = 5
            override val label = "five"
            override suspend fun run() = fail("should not run")
        }

        coEvery { reader.read() } returnsMany listOf("99", "")
        menu = MainMenuUI(listOf(dummy), viewer, reader)

        menu.run()

        assert(printed.any { it.contains("Invalid choice: 99") })
    }

    @Test
    fun `trimmed lowercase x also exits`() = runTest {
        coEvery { reader.read() } returnsMany listOf("  x  ")
        menu = MainMenuUI(emptyList(), viewer, reader)

        menu.run()

        assert(printed.size >= 1)
    }

    @Test
    fun `exit on null input`() = runTest {
        coEvery { reader.read() } returns null

        val dummy = object : UiRunner {
            override val id = 1
            override val label = "one"
            var ran = false
            override suspend fun run() { ran = true }
        }
        menu = MainMenuUI(listOf(dummy), viewer, reader)

        menu.run()

        assertThat(dummy.ran).isFalse()
        assertThat(printed.first()).contains("=== Task Manager ===")
    }

    @Test
    fun `menu has correct id and label`() {
        coEvery { reader.read() } returns ""
        menu = MainMenuUI(emptyList(), viewer, reader)

        assertThat(menu.id).isEqualTo(0)
        assertThat(menu.label).isEqualTo("Main menu")
    }
}