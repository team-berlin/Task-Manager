package presentation.task

import com.berlin.domain.model.*
import com.berlin.presentation.task.GetTasksByProjectIdUI
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import org.berlin.data.DummyData
import org.berlin.presentation.input_output.Reader
import org.berlin.presentation.input_output.Viewer
import org.junit.jupiter.api.*

class GetTasksByProjectIdUITest {

    private val printed = mutableListOf<String>()

    private val viewer: Viewer = mockk(relaxed = true) {
        every { show(capture(printed)) } just Runs
    }
    private val reader: Reader = mockk()

    private lateinit var ui: GetTasksByProjectIdUI

    private val projectP1 = Project("P1", "Core", null, emptyList(), emptyList())
    private val stateTodo = State("S1", "TODO", "P1")
    private val alice = User("U1", "alice", "pw", UserRole.MATE)

    @BeforeEach
    fun setUp() {
        DummyData.projects.clear()
        DummyData.states.clear()
        DummyData.tasks.clear()
        printed.clear()

        DummyData.projects += projectP1
        DummyData.states += stateTodo
        DummyData.users

        ui = GetTasksByProjectIdUI(viewer, reader)
    }

    @Test
    fun `shows swimlane with one task`() {
        val task = Task(
            id = "T1",
            projectId = "P1",
            title = "Feature",
            description = null,
            stateId = "S1",
            assignedToUserId = alice.id,
            createByUserId = alice.id
        )
        DummyData.tasks += task

        every { reader.read() } returns "1"

        ui.run()

        assertThat(printed).contains("\n=== Tasks for project P1 ===")
        assertThat(printed).contains("- T1: Feature  → ${alice.id}")
    }


    @Test
    fun `prints no states message`() {
        DummyData.states.clear()
        every { reader.read() } returns "1"

        ui.run()

        assertThat(printed.last()).contains("No states found")
    }

    @Test
    fun `user cancellation prints Cancelled`() {
        every { reader.read() } returns "X"

        ui.run()

        assertThat(printed.last()).contains("Cancelled")
    }

    @Test
    fun `invalid choice prints placeholder text`() {
        every { reader.read() } returns "99"

        ui.run()

        assertThat(printed.last()).contains("{ex.message}")
    }


    @Test
    fun `state with zero tasks prints placeholder`() {
        every { reader.read() } returns "1"

        ui.run()

        assertThat(printed).contains("  (no tasks)")
    }

}
