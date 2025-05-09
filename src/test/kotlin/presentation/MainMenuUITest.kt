package presentation

import com.berlin.domain.model.User
import com.berlin.domain.model.UserRole
import com.berlin.presentation.MainMenuUI
import com.berlin.presentation.UiRunner
import com.berlin.presentation.authService.AuthenticateUserUI
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import com.google.common.truth.Truth.assertThat
import data.UserCache
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class MainMenuUITest {

    private lateinit var viewer: Viewer
    private lateinit var reader: Reader
    private lateinit var menu: MainMenuUI
    private lateinit var authUi: AuthenticateUserUI
    private lateinit var userCache: UserCache

    private val printed = mutableListOf<String>()

    @BeforeEach
    fun setUp() {
        viewer = mockk(relaxed = true) {
            every { show(capture(printed)) } just Runs
        }
        reader = mockk()
        authUi = mockk()
        userCache = UserCache(User("Y1", "menna", "12345678", UserRole.ADMIN))
        userCache.currentUser = User("Y1", "menna", "12345678", UserRole.ADMIN)
    }


    @Test
    fun `run should return user role when successfully logged in`() {
        //given
        every { viewer.show("") }
        every { authUi.run() } just Runs
        every { viewer.show("") }
        every { reader.read() } returns "X"
        menu = MainMenuUI(emptyList(), viewer, reader, authUi, userCache)

        //when
        menu.run()

        //Then
        assert(printed.contains("===ADMIN Board==="))
    }

    @Test
    fun `run should exit immediately on blank input without running any runner `() {
        //Given
        every { viewer.show("") }
        every { authUi.run() } just Runs
        every { viewer.show("") }
        every { reader.read() } returns "X"
        menu = MainMenuUI(emptyList(), viewer, reader, authUi, userCache)

        //when
        menu.run()

        //Then
        assert(printed.first().contains("===Welcome to our PlanMate==="))


    }

    @Test
    fun `runs matching runner then exits on X`() {
        every { viewer.show("") }
        every { authUi.run() } just Runs
        every { viewer.show("") }
        val r0 = object : UiRunner {
            override val id = 0
            override val label = "zero"
            var invoked = 0
            override fun run() {
                invoked++
            }
        }
        val r1 = object : UiRunner {
            override val id = 1
            override val label = "one"
            var invoked = 0
            override fun run() {
                invoked++
            }
        }

        every { reader.read() } returnsMany listOf("1", "X")
        menu = MainMenuUI(listOf(r0, r1), viewer, reader, authUi, userCache)

        menu.run()

        assert(r1.invoked == 1)
        assert(r0.invoked == 0)

    }

    @Test
    fun `invalid choice prints error then exits`() {
        every { viewer.show("") }
        every { authUi.run() } just Runs
        every { viewer.show("") }
        val dummy = object : UiRunner {
            override val id = 5
            override val label = "five"
            override fun run() = fail("should not run")
        }

        every { reader.read() } returnsMany listOf("99", "")
        menu = MainMenuUI(listOf(dummy), viewer, reader, authUi, userCache)

        menu.run()

        assert(printed.any { it.contains("Invalid choice: 99") })
    }

    @Test
    fun `trimmed lowercase x also exits`() {
        every { viewer.show("") }
        every { authUi.run() } just Runs
        every { viewer.show("") }
        every { reader.read() } returnsMany listOf("  x  ")
        menu = MainMenuUI(emptyList(), viewer, reader, authUi, userCache)

        menu.run()

        assert(printed.size >= 1)
    }

    @Test
    fun `exit on null input`() {
        every { viewer.show("") }
        every { authUi.run() } just Runs
        every { viewer.show("") }
        every { reader.read() } returns null

        val dummy = object : UiRunner {
            override val id = 1
            override val label = "one"
            var ran = false
            override fun run() {
                ran = true
            }
        }
        menu = MainMenuUI(listOf(dummy), viewer, reader, authUi, userCache)

        menu.run()

        assertThat(dummy.ran).isFalse() }

    @Test
    fun `menu has correct id and label`() {
        every { reader.read() } returns ""
        menu = MainMenuUI(emptyList(), viewer, reader, authUi, userCache)

        assertThat(menu.id).isEqualTo(0)
        assertThat(menu.label).isEqualTo("Main menu")
    }
}
