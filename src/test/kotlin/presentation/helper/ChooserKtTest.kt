package presentation.helper


import com.berlin.domain.exception.InputCancelledException
import com.berlin.domain.exception.InvalidSelectionException
import com.berlin.presentation.helper.choose
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ChooseHelperTest {

    private lateinit var viewer: Viewer
    private lateinit var reader: Reader
    private val printed = mutableListOf<String>()

    @BeforeEach
    fun setUp() {
        viewer = mockk(relaxed = true) {
            coEvery { show(capture(printed)) } just Runs
        }
        reader = mockk()
        printed.clear()
    }

    @Test
    fun `throws when elements is empty`() {
        val ex = assertThrows<InvalidSelectionException> {
            choose("Things", emptyList<String>(), { it }, viewer, reader)
        }
        assertThat(ex.message).isEqualTo("No Things available.")
    }

    @Test
    fun `valid numeric choice returns element and prints menu`() {
        val items = listOf("apple", "banana", "cherry")
        coEvery { reader.read() } returns "2"

        val picked = choose("Fruits", items, { it.uppercase() }, viewer, reader)

        assertThat(picked).isEqualTo("banana")

        assertThat(printed).containsExactly(
            "--- Fruits ---", "1. APPLE", "2. BANANA", "3. CHERRY", "X – Cancel\nSelect:"
        ).inOrder()
    }

    @Test
    fun `trimmed input and lowercase x cancels`() {
        val items = listOf("a", "b")
        coEvery { reader.read() } returns "  x  "

        assertThrows<InputCancelledException> {
            choose("Letters", items, { it }, viewer, reader)
        }
        assertThat(printed.first()).isEqualTo("--- Letters ---")
    }

    @Test
    fun `non-numeric input throws Not a number`() {
        val items = listOf(1, 2, 3)
        coEvery { reader.read() } returns "foo"

        val ex = assertThrows<InvalidSelectionException> {
            choose("Numbers", items, { it.toString() }, viewer, reader)
        }
        assertThat(ex.message).isEqualTo("Not a number.")
    }

    @Test
    fun `numeric but out-of-range throws Out of range`() {
        val items = listOf("x", "y")
        coEvery { reader.read() } returns "5"

        val ex = assertThrows<InvalidSelectionException> {
            choose("Chars", items, { it }, viewer, reader)
        }
        assertThat(ex.message).isEqualTo("Out of range.")
    }
}
