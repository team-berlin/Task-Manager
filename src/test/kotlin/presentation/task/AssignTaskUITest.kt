package presentation.task

import com.berlin.domain.model.*
import com.berlin.domain.usecase.task.AssignTaskUseCase
import com.berlin.presentation.task.AssignTaskUI
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import org.berlin.data.DummyData
import org.berlin.presentation.input_output.Reader
import org.berlin.presentation.input_output.Viewer
import org.junit.jupiter.api.*


class AssignTaskUITest {

    private val printed = mutableListOf<String>()

    private val viewer: Viewer = mockk(relaxed = true) {
        every { show(capture(printed)) } just Runs
    }
    private val reader: Reader = mockk()

    private val assignTaskUC: AssignTaskUseCase = mockk()
    private lateinit var task: Task
    private lateinit var newAssignee: User

    @BeforeEach
    fun setUp() {
        DummyData.tasks.clear()

        newAssignee = DummyData.users[1]
        task = Task(
            id = "1",
            projectId = "P1",
            title = "Demo",
            description = null,
            stateId = "TODO",
            assignedToUserId = DummyData.users[0].id,
            createByUserId = DummyData.users[0].id
        )
        DummyData.tasks += task
    }

    @Test
    fun `repository update is called with new assignee`() {
        every { reader.read() } returnsMany listOf("1", "2")

        every { assignTaskUC.invoke(task.id, newAssignee) } returns Result.success(task.copy(assignedToUserId = newAssignee.id))

        AssignTaskUI(assignTaskUC, viewer, reader).run()

        verify(exactly = 1) { assignTaskUC.invoke(task.id, newAssignee) }
        assertThat(printed).contains("Assigned to ${newAssignee.userName}")
    }

    @Test
    fun `user cancels in first chooser`() {
        every { reader.read() } returns "X"

        AssignTaskUI(assignTaskUC, viewer, reader).run()

        verify { assignTaskUC wasNot Called }
        assertThat(printed.last()).contains("Cancelled")
    }

    @Test
    fun `error from use case is shown to the user`() {
        every { reader.read() } returnsMany listOf("1", "2")

        val boom = IllegalStateException("cant assign")
        every { assignTaskUC.invoke(task.id, newAssignee) } returns Result.failure(boom)

        AssignTaskUI(assignTaskUC, viewer, reader).run()

        verify { assignTaskUC.invoke(task.id, newAssignee) }
        assertThat(printed.last()).contains("cant assign")
    }

    @Test
    fun `invalid index prints error message`() {
        every { reader.read() } returns "36"

        AssignTaskUI(assignTaskUC, viewer, reader).run()

        verify { assignTaskUC wasNot Called }
        assertThat(printed.last().lowercase()).contains("out of range")
    }

}