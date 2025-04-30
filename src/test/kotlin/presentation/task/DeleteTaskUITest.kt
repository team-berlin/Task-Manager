package presentation.task

import com.berlin.domain.model.Task
import com.berlin.domain.usecase.task.DeleteTaskUseCase
import com.berlin.presentation.task.DeleteTaskUI
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import org.berlin.data.DummyData
import org.berlin.presentation.input_output.Reader
import org.berlin.presentation.input_output.Viewer
import org.junit.jupiter.api.*

class DeleteTaskUITest {

    private val printed = mutableListOf<String>()

    private val viewer: Viewer = mockk(relaxed = true) {
        every { show(capture(printed)) } just Runs
    }
    private val reader: Reader = mockk()

    private val deleteUC: DeleteTaskUseCase = mockk()

    private lateinit var theTask: Task
    private lateinit var ui: DeleteTaskUI

    @BeforeEach
    fun setUp() {
        DummyData.tasks.clear()
        printed.clear()

        theTask = Task(
            id = "T1",
            projectId = "P1",
            title = "Demo",
            description = null,
            stateId = "TODO",
            assignedToUserId = "U1",
            createByUserId = "U1"
        )
        DummyData.tasks += theTask

        ui = DeleteTaskUI(deleteUC, viewer, reader)
    }

    @Test
    fun `deletes task and prints confirmation`() {
        every { reader.read() } returnsMany listOf("1", "y")
        every { deleteUC.invoke(theTask.id) } returns Result.success(Unit)

        ui.run()

        verify { deleteUC.invoke(theTask.id) }
        assertThat(DummyData.tasks).doesNotContain(theTask)
        assertThat(printed.last()).contains("Deleted")
    }

    @Test
    fun `user aborts deletion at confirmation`() {
        every { reader.read() } returnsMany listOf("1", "n")

        ui.run()

        verify { deleteUC wasNot Called }
        assertThat(DummyData.tasks).contains(theTask)
        assertThat(printed.last()).contains("Cancelled")
    }

    @Test
    fun `user cancels in chooser`() {
        every { reader.read() } returns "X"

        ui.run()

        verify { deleteUC wasNot Called }
        assertThat(printed.last()).contains("Cancelled")
    }

    @Test
    fun `failure from use case is shown`() {
        every { reader.read() } returnsMany listOf("1", "y")
        every { deleteUC.invoke(theTask.id) } returns Result.failure(IllegalStateException("cannot delete"))

        ui.run()

        verify { deleteUC.invoke(theTask.id) }
        assertThat(DummyData.tasks).contains(theTask)
        assertThat(printed.last()).contains("cannot delete")
    }


    @Test
    fun `invalid index prints error message`() {
        every { reader.read() } returns "99"

        ui.run()

        verify { deleteUC wasNot Called }
        assertThat(printed.last()).contains("range")
    }

}
